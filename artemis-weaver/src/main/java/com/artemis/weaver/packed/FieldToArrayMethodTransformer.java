package com.artemis.weaver.packed;

import static com.artemis.weaver.packed.InstructionMutator.on;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.MethodTransformer;

public class FieldToArrayMethodTransformer extends MethodTransformer implements Opcodes {

	private final ClassMetadata meta;
	private final String fieldDesc;
	private final List<String> dataFieldNames;

	public FieldToArrayMethodTransformer(MethodTransformer mt, ClassMetadata meta, List<String> dataFieldNames) {
		super(mt);
		this.meta = meta;
		this.dataFieldNames = dataFieldNames;
		
		FieldDescriptor f = ClassMetadataUtil.instanceFields(meta).get(0);
		fieldDesc = f.desc;
	}
	
	
	@Override
	public void transform(MethodNode mn) {
		InsnList instructions = mn.instructions;
		
		for (int i = 0; instructions.size() > i; i++) {
			AbstractInsnNode node = instructions.get(i);
			switch(node.getType()) {
				case AbstractInsnNode.FIELD_INSN:
					FieldInsnNode f = (FieldInsnNode)node;
					String owner = meta.type.getInternalName();
					if (isSettingField(f)) {
						i = on(instructions, f)
							.insertAtOffset(2,
								new FieldInsnNode(GETSTATIC, owner, "$data", "[" + fieldDesc))
							.insertAtOffset(1,
								new FieldInsnNode(GETFIELD, owner, "$offset", "I"),
								new InsnNode(ICONST_0 + dataFieldNames.indexOf(f.name)),
								new InsnNode(IADD))
							.insertAtOffset(0,
								new InsnNode(FASTORE))
							.delete(0)
							.transform();
					} else if (isGettingField(f)) {
						i = on(instructions, f)
							.insertAtOffset(1, 
								new FieldInsnNode(GETSTATIC, owner, "$data", "[" + fieldDesc))
							.insertAtOffset(0,
								new FieldInsnNode(GETFIELD, owner, "$offset", "I"),
								new InsnNode(ICONST_0 + dataFieldNames.indexOf(f.name)),
								new InsnNode(IADD),
								new InsnNode(FALOAD))
							.delete(0)
							.transform();
					}
					break;
				default:
					break;
			}
		}
		
		super.transform(mn);
	}

	private boolean isSettingField(FieldInsnNode f) {
		return PUTFIELD == f.getOpcode() &&
			f.owner.equals(meta.type.getInternalName()) &&
			f.desc.equals(fieldDesc) &&
			hasInstanceField(meta, f.name);
	}
	
	private boolean isGettingField(FieldInsnNode f) {
		return GETFIELD == f.getOpcode() &&
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
}
