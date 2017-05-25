package com.artemis.weaver.optimizer;

import com.artemis.meta.ClassMetadata;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SystemMethodVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;

	public SystemMethodVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM5, mv);
		this.meta = meta;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		if (opcode == INVOKESPECIAL) {
			EntitySystemType resolved = EntitySystemType.resolve(owner);
			if (resolved != null) {
				owner = resolved.replacedSuperName;
			}
		}
		
		mv.visitMethodInsn(opcode, owner, name, desc, itf);
	}
}
