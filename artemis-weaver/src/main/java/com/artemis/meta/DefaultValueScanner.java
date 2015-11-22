package com.artemis.meta;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class DefaultValueScanner extends MethodVisitor implements Opcodes {
	private final ClassMetadata meta;

	private AbstractInsnNode node;

	public DefaultValueScanner(MethodVisitor parent, ClassMetadata meta) {
		super(ASM5, parent);
		this.meta = meta;
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		node = new VarInsnNode(opcode, var);
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		node = new LdcInsnNode(cst);
		super.visitLdcInsn(cst);
	}

	@Override
	public void visitInsn(int opcode) {
		node = new InsnNode(opcode);
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		node = new IntInsnNode(opcode, operand);
		super.visitIntInsn(opcode, operand);
	}


	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if (meta.type.getInternalName().equals(owner)) {
			FieldDescriptor fd = meta.field(name);
			if (fd.isResettable()) {
				fd.reset = node;
				node = null;
			}
		}
		super.visitFieldInsn(opcode, owner, name, desc);
	}
}
