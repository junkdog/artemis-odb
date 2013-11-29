package com.artemis.weaver;

import static com.artemis.meta.ClassMetadataUtil.hasGetter;
import static com.artemis.meta.ClassMetadataUtil.hasSetter;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.FieldDescriptor;

final class AccessorGenerator implements Opcodes {

	private final ClassReader cr;
	private final ClassMetadata meta;
	private final ClassWriter cw;

	public AccessorGenerator(ClassReader cr, ClassMetadata meta) {
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cr = cr;
		this.meta = meta;
	}

	public ClassReader transform() {
		return new ClassReader(injectAccessors());
	}

	private byte[] injectAccessors() {
		List<FieldDescriptor> fields = instanceFields(meta);
		for (FieldDescriptor field : fields) {
			getterFor(field);
			setterFor(field);
		}
		
		cr.accept(cw, 0);
		return cw.toByteArray();
	}

	private void setterFor(FieldDescriptor f) {
		if (hasSetter(meta, f))
			return;
		
		TypedOpcodes opcodes = new TypedOpcodes(f);
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, f.name, "(" + f.desc + ")V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(opcodes.tLOAD(), 1);
		mv.visitFieldInsn(PUTFIELD, meta.type.getInternalName(), f.name, f.desc);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", meta.type.getDescriptor(), null, l0, l2, 0);
		mv.visitLocalVariable(f.name, f.desc, null, l0, l2, 1);
		mv.visitEnd();
	}

	private void getterFor(FieldDescriptor f) {	
		if (hasGetter(meta, f))
			return;
		
		TypedOpcodes opcodes = new TypedOpcodes(f);
		
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, f.name, "()" + f.desc, null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, meta.type.getInternalName(), f.name, f.desc);
		mv.visitInsn(opcodes.tRETURN());
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", meta.type.getDescriptor(), null, l0, l1, 0);
		mv.visitEnd();
	}
}
