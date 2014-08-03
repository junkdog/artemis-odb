package com.artemis.weaver;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;

public class ConstructorInvocationVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;

	public ConstructorInvocationVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM4, mv);
		this.meta = meta;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (PACKED == meta.annotation) {
			mv.visitMethodInsn(opcode, owner(owner), name, desc);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitIntInsn(SIPUSH, 1024); // 8 * 128
			mv.visitMethodInsn(INVOKESTATIC, "java/nio/ByteBuffer", "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
			mv.visitFieldInsn(PUTFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		} else {
			mv.visitMethodInsn(opcode, owner, name, desc);
		}
	}

	private String owner(String owner) {
		if (owner.equals(meta.type.getInternalName()))
			return owner;
		
		switch (meta.annotation) {
			case PACKED:
				return "com/artemis/PackedComponent";
			case POOLED:
				return "com/artemis/PooledComponent";
			default:
				throw new RuntimeException("Failed transforming super class from '" + owner + "'");
		}
	}
}
