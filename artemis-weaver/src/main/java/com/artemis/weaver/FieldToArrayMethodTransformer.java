package com.artemis.weaver;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.MethodTransformer;

public class FieldToArrayMethodTransformer extends MethodTransformer implements Opcodes {

	private final ClassMetadata meta;
	private final String fieldDesc;

	public FieldToArrayMethodTransformer(MethodTransformer mt, ClassMetadata meta) {
		super(mt);
		this.meta = meta;
		
		FieldDescriptor f = ClassMetadataUtil.instanceFields(meta).get(0);
		fieldDesc = f.desc;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void transform(MethodNode mn) {
		InsnList instructions = mn.instructions;
		ListIterator<AbstractInsnNode> it = instructions.iterator();
		
		while (it.hasNext()) {
			AbstractInsnNode node = it.next();
			if (node instanceof FieldInsnNode) {
				FieldInsnNode f = (FieldInsnNode)node;
				if (isSettingField(f)) {
//					AbstractInsnNode aload = instructions.get(0);
//					instructions.insertBefore(aload, 
//						new FieldInsnNode(GETSTATIC, meta.type.getInternalName(), "$data", "[" + fieldDesc));
					
//					InsnList dataInsn = new InsnList();
//					dataInsn.add(new FieldInsnNode(GETSTATIC, meta.type.getInternalName(), "$offset", "I"));
//					dataInsn.add(new InsnNode(ICONST_0)); //TODO head offsets
//					dataInsn.add(new InsnNode(IADD));
					
//					instructions.insert(aload, dataInsn);
					it.previous();
					it.previous();
					it.previous();
					it.add(new FieldInsnNode(GETSTATIC, meta.type.getInternalName(), "$data", "[" + fieldDesc));
					it.next();
					it.add(new FieldInsnNode(GETFIELD, meta.type.getInternalName(), "$offset", "I"));
					it.add(new InsnNode(ICONST_0)); //TODO head offsets
					it.add(new InsnNode(IADD));
					it.next();
					it.next();
					
					it.set(new InsnNode(FASTORE));
				}
				if (isGettingField(f)) {
					it.previous();
					it.previous();
					it.add(new FieldInsnNode(GETSTATIC, meta.type.getInternalName(), "$data", "[" + fieldDesc));
					it.next();
					it.add(new FieldInsnNode(GETFIELD, meta.type.getInternalName(), "$offset", "I"));
					it.add(new InsnNode(ICONST_0)); //TODO head offsets
					it.add(new InsnNode(IADD));
					it.next();
					
					it.set(new InsnNode(FALOAD));
				}
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
	
	private static int constInstructionFor(FieldDescriptor field) {
		if ("Z".equals(field.desc))
			return ICONST_0;
		if ("C".equals(field.desc))
			return ICONST_0;
		if ("S".equals(field.desc))
			return ICONST_0;
		if ("I".equals(field.desc))
			return ICONST_0;
		if ("J".equals(field.desc))
			return LCONST_0;
		if ("F".equals(field.desc))
			return FCONST_0;
		if ("D".equals(field.desc))
			return DCONST_0;
		else
			return ACONST_NULL;
	}
	
	private static boolean hasInstanceField(ClassMetadata meta, String fieldName) {
		for (FieldDescriptor f : ClassMetadataUtil.instanceFields(meta)) {
			if (f.name.equals(fieldName))
				return true;
		}
		
		return false;
	}
}
