package com.artemis.weaver.packed;

import static com.artemis.weaver.packed.InstructionMutator.on;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.MethodTransformer;

public class FieldToStructMethodTransformer extends MethodTransformer implements Opcodes {

	private final ClassMetadata meta;
	private final String fieldDesc;
	
	private static final String BYTEBUFFER_DESC = "Ljava/nio/ByteBuffer;";
	
	private static final boolean LOG = false;

	public FieldToStructMethodTransformer(MethodTransformer mt, ClassMetadata meta, FieldDescriptor f) {
		super(mt);
		this.meta = meta;
		
		fieldDesc = f.desc;
	}
	
	@Override
	public boolean transform(MethodNode mn) {
		InsnList instructions = mn.instructions;
		String owner = meta.type.getInternalName();
		
		if (LOG) System.out.println("OWNER: " + owner + " " + mn.name);
		
		ByteBufferHelper bufferHelper = new ByteBufferHelper(meta);
		
		boolean shouldDoSetter = true;
		for (int i = 0; instructions.size() > i; i++) {
			AbstractInsnNode node = instructions.get(i);
			switch(node.getType()) {
				case AbstractInsnNode.FIELD_INSN:
					FieldInsnNode f = (FieldInsnNode)node;
					if (shouldDoSetter && isSettingFieldWithPrimitive(f)) {
						if (LOG) System.out.println(">> SETTING FIELD index=" + i);
						i = on(instructions, f)
							.insertAtOffset(2,
								new VarInsnNode(ALOAD, 0),
								new FieldInsnNode(GETFIELD, owner, "$data", BYTEBUFFER_DESC))
							.insertAtOffset(1,
								new FieldInsnNode(GETFIELD, owner, "$stride", "I"),
								fieldOffsetInstruction(f.name),
								new InsnNode(IADD))
							.insertAtOffset(0,
								bufferHelper.invokePutter(f.name),
								new InsnNode(POP))
							.delete(0)
							.transform();
					} else if (!shouldDoSetter && isSettingFieldWithPrimitive(f)) {
						if (LOG) System.out.println(">> SETTING FIELD index=" + i);
						i = on(instructions, f)
							.insertAtOffset(0,
								bufferHelper.invokePutter(f.name),
								new InsnNode(POP))
							.delete(0)
							.transform();
					} else if (isSettingFieldWithObject(f)) {
						if (LOG) System.out.println(">> SETTING FIELD FROM OBJECT index=" + i);
						i = on(instructions, f)
							.insertAtOffset(3,
								new VarInsnNode(ALOAD, 0),
								new FieldInsnNode(GETFIELD, owner, "$data", BYTEBUFFER_DESC))
							.insertAtOffset(2,
								new FieldInsnNode(GETFIELD, owner, "$stride", "I"),
								fieldOffsetInstruction(f.name),
								new InsnNode(IADD))
							.insertAtOffset(0, 
								bufferHelper.invokePutter(f.name),
								new InsnNode(POP))
							.delete(0)
							.transform();
					} else if (isModifyingFieldWithObject(f)) {
						if (LOG) System.out.println(">> SETTING-MODIFYING FIELD FROM OBJECT index=" + i);
						i = on(instructions, f)
							.insertAtOffset(6,
								new VarInsnNode(ALOAD, 0),
								new FieldInsnNode(GETFIELD, owner, "$data", BYTEBUFFER_DESC))
							.insertAtOffset(5,
								new FieldInsnNode(GETFIELD, owner, "$stride", "I"),
								fieldOffsetInstruction(f.name),
								new InsnNode(IADD),
								new InsnNode(DUP2),
								bufferHelper.invokeGetter(f.name))
							.insertAtOffset(0, 
								bufferHelper.invokePutter(f.name),
								new InsnNode(POP))
							.delete(5)
							.delete(4)
							.delete(0)
							.transform();
					} else if (isLoadingFromField(f)) {
						if (LOG) System.out.println("<< LOAD FIELD index=" + i);
						i = on(instructions, f)
							.insertAtOffset(2, 
								new VarInsnNode(ALOAD, 0),
								new FieldInsnNode(GETFIELD, owner, "$data", BYTEBUFFER_DESC))
							.insertAtOffset(0,
								new FieldInsnNode(GETFIELD, owner, "$stride", "I"),
								fieldOffsetInstruction(f.name),
								new InsnNode(IADD),
								new InsnNode(DUP2),
								bufferHelper.invokeGetter(f.name))
							.delete(1)
							.delete(0)
							.transform();
						shouldDoSetter = false;
					} else if (isGettingField(f)) {
						if (LOG) System.out.println("<< GETTING FIELD index=" + i);
						i = on(instructions, f)
							.insertAtOffset(1, 
								new VarInsnNode(ALOAD, 0),
								new FieldInsnNode(GETFIELD, owner, "$data", BYTEBUFFER_DESC))
							.insertAtOffset(0,
								new FieldInsnNode(GETFIELD, owner, "$stride", "I"),
								fieldOffsetInstruction(f.name),
								new InsnNode(IADD),
								bufferHelper.invokeGetter(f.name))
							.delete(0)
							.transform();
					}
					if (LOG) System.out.println("\tindex=" + i);
					break;
				default:
					break;
			}
		}
		
		return super.transform(mn);
	}

	private boolean isSettingFieldWithPrimitive(FieldInsnNode f) {
		return PUTFIELD == f.getOpcode() &&
			f.owner.equals(meta.type.getInternalName()) &&
			f.desc.equals(fieldDesc) &&
			hasInstanceField(meta, f.name) &&
			!isObjectAccess(f.getPrevious()) &&
			!isObjectAccess(f.getPrevious().getPrevious());
	}
	
	private boolean isSettingFieldWithObject(FieldInsnNode f) {
		return PUTFIELD == f.getOpcode() &&
			isObjectAccess(f.getPrevious()) &&
			f.owner.equals(meta.type.getInternalName()) &&
			f.desc.equals(fieldDesc) &&
			hasInstanceField(meta, f.name);
	}
	
	private boolean isModifyingFieldWithObject(FieldInsnNode f) {
		return PUTFIELD == f.getOpcode() &&
			isObjectAccess(f.getPrevious().getPrevious()) &&
			f.owner.equals(meta.type.getInternalName()) &&
			f.desc.equals(fieldDesc) &&
			hasInstanceField(meta, f.name);
	}


	private boolean isLoadingFromField(FieldInsnNode f) {
		return GETFIELD == f.getOpcode() &&
			DUP == f.getPrevious().getOpcode() &&
			!isObjectAccess(f.getNext().getNext()) &&
			f.owner.equals(meta.type.getInternalName()) &&
			f.desc.equals(fieldDesc) &&
			hasInstanceField(meta, f.name);
	}
	
	private boolean isGettingField(FieldInsnNode f) {
		return GETFIELD == f.getOpcode() &&
			DUP != f.getPrevious().getOpcode() &&
			f.owner.equals(meta.type.getInternalName()) &&
			f.desc.equals(fieldDesc) &&
			hasInstanceField(meta, f.name);
	}
	
	
	private static boolean hasInstanceField(ClassMetadata meta, String fieldName) {
		for (FieldDescriptor f : ClassMetadataUtil.instanceFields(meta)) {
			if (f.name.equals(fieldName))
				return true;
		}
		
		return false;
	}
	
	private boolean isObjectAccess(AbstractInsnNode n) {
		if (n == null) return false;
		
		int opcode = n.getOpcode();
		return 
			opcode == INVOKESPECIAL ||
			opcode == INVOKEVIRTUAL ||
			opcode == INVOKEINTERFACE ||
			(opcode == GETFIELD && !((FieldInsnNode)n).owner.equals(meta.type.getInternalName()));
	}
	
	private AbstractInsnNode fieldOffsetInstruction(String name) {
		int offset = offset(name);
		
		if (offset <= 5)
			return new InsnNode(ICONST_0 + offset);
		else if (offset <= 0xff)
			return new IntInsnNode(BIPUSH, offset);
		else
			return new IntInsnNode(SIPUSH, offset);
	}
	
	private int offset(String name) {
		List<FieldDescriptor> fields = meta.fields();
		
		int offset = 0;
		for (int i = 0; fields.size() > i; i++) {
			FieldDescriptor fd = fields.get(i);
			if (fd.name.equals(name))
				break;
			
			offset += ClassMetadataUtil.sizeOf(fd);
		}
		return offset;
	}
}
