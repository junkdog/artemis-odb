package com.artemis.weaver.transplant;

import com.artemis.meta.ClassMetadata;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodBodyTransplanter extends MethodVisitor {
	private final String owner;
	private final String oldOwner;
	private final Type type;

	public MethodBodyTransplanter(String oldOwner, ClassMetadata meta, MethodVisitor mv) {
		super(Opcodes.ASM5, mv);
		this.oldOwner = oldOwner;
		type = meta.type;
		this.owner = meta.type.getInternalName();
	}

	public MethodBodyTransplanter(Class<?> oldOwner, Type newType, MethodVisitor mv) {
		super(Opcodes.ASM5, mv);
		this.oldOwner = Type.getType(oldOwner).getInternalName();
		this.type = newType;
		this.owner = newType.getInternalName();
	}



	@Override
	public void visitLineNumber(int line, Label start) {
		// remove line numbers
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if (oldOwner.equals(owner))
			owner = this.owner;

		super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		if (local.length > 0 && oldOwner.equals(local[0]))
			local[0] = owner;

		super.visitFrame(type, nLocal, local, nStack, stack);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		if (oldOwner.equals(owner))
			owner = this.owner;

		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if ("this".equals(name))
			desc = type.toString();

		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		if (oldOwner.equals(type))
			type = owner;

		super.visitTypeInsn(opcode, type);
	}
}
