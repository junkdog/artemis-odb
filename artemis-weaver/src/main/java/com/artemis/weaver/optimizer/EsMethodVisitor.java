package com.artemis.weaver.optimizer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;

public class EsMethodVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;

	public EsMethodVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM4, mv);
		this.meta = meta;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (opcode == INVOKESPECIAL && owner.equals("com/artemis/systems/EntityProcessingSystem")) {
			owner = "com/artemis/EntitySystem";
		}
		
		mv.visitMethodInsn(opcode, owner, name, desc);
	}
}
