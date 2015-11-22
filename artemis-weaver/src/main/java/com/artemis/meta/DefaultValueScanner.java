package com.artemis.meta;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DefaultValueScanner extends MethodVisitor implements Opcodes {
	private final ClassMetadata meta;

	public DefaultValueScanner(ClassMetadata meta) {
		super(ASM5);
		this.meta = meta;
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		// mv.visitVarInsn(ALOAD, 0); - begin
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		super.visitLdcInsn(cst);
	}

	@Override
	public void visitInsn(int opcode) {
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		super.visitIntInsn(opcode, operand);
	}


	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		super.visitFieldInsn(opcode, owner, name, desc);
	}
}
