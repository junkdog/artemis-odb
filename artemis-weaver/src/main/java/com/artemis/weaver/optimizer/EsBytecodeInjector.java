package com.artemis.weaver.optimizer;

import com.artemis.ClassUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.OptimizationType;

public final class EsBytecodeInjector implements Opcodes {

	private final ClassReader cr;
	private final ClassMetadata meta;
	private final ClassWriter cw;

	public EsBytecodeInjector(ClassReader cr, ClassMetadata meta) {
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cr = cr;
		this.meta = meta;
	}

	public ClassReader transform() {
		return new ClassReader(injectMethods());
	}

	private byte[] injectMethods() {
		cw.visitField(ACC_PRIVATE, "flyweight", "Lcom/artemis/Entity;", null, null).visitEnd();

		injectSetWorld();
		injectProcessEntities();
		cr.accept(cw, 0);
		return cw.toByteArray();
	}

	private void injectSetWorld() {
		String owner = meta.type.getInternalName();

		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED | ACC_FINAL,
				"setWorld", "(Lcom/artemis/World;)V", null, null);
		mv.visitCode();

		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(33, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESPECIAL, "com/artemis/EntitySystem", "setWorld", "(Lcom/artemis/World;)V", false);

		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(34, l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, owner, "createFlyweightEntity", "()Lcom/artemis/Entity;", false);
		mv.visitFieldInsn(PUTFIELD, owner, "flyweight", "Lcom/artemis/Entity;");

		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(35, l2);
		mv.visitInsn(RETURN);

		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l3, 0);
		mv.visitLocalVariable("world", "Lcom/artemis/World;", null, l0, l3, 1);
		mv.visitEnd();
	}

	private void injectProcessEntities() {
		String owner = meta.type.getInternalName();

		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED | ACC_FINAL,
				"processSystem", "()V", null, null);
		mv.visitCode();

		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "subscription", "Lcom/artemis/EntitySubscription;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/EntitySubscription",
				"getEntities", "()Lcom/artemis/utils/IntBag;", false);
		mv.visitVarInsn(ASTORE, 1);

		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(48, l1);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/IntBag", "getData", "()[I", false);
		mv.visitVarInsn(ASTORE, 2);

		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(49, l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "flyweight", "Lcom/artemis/Entity;");
		mv.visitVarInsn(ASTORE, 3);

		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLineNumber(50, l3);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 4);

		Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/IntBag", "size", "()I", false);
		mv.visitVarInsn(ISTORE, 5);

		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitFrame(F_FULL, 6, new Object[]{
				owner, "com/artemis/utils/IntBag", "[I", "com/artemis/Entity",
				INTEGER, INTEGER}, 0, new Object[]{});
		mv.visitVarInsn(ILOAD, 5);
		mv.visitVarInsn(ILOAD, 4);

		Label l6 = new Label();
		mv.visitJumpInsn(IF_ICMPLE, l6);

		Label l7 = new Label();
		mv.visitLabel(l7);
		mv.visitLineNumber(51, l7);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitInsn(IALOAD);
		mv.visitFieldInsn(PUTFIELD, "com/artemis/Entity", "id", "I");

		Label l8 = new Label();
		mv.visitLabel(l8);
		mv.visitLineNumber(52, l8);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(invocation(meta.sysetemOptimizable),
				owner, "process", "(Lcom/artemis/Entity;)V", false);

		Label l9 = new Label();
		mv.visitLabel(l9);
		mv.visitLineNumber(50, l9);
		mv.visitIincInsn(4, 1);
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l6);
		mv.visitLineNumber(54, l6);
		mv.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
		mv.visitInsn(RETURN);

		Label l10 = new Label();
		mv.visitLabel(l10);
		mv.visitLocalVariable("i", "I", null, l4, l6, 4);
		mv.visitLocalVariable("s", "I", null, l5, l6, 5);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l10, 0);
		mv.visitLocalVariable("actives", "Lcom/artemis/utils/IntBag;", null, l1, l10, 1);
		mv.visitLocalVariable("array", "[I", null, l2, l10, 2);
		mv.visitLocalVariable("e", "Lcom/artemis/Entity;", null, l3, l10, 3);
		mv.visitEnd();

//		mv.visitLabel(l0);
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitFieldInsn(GETFIELD, owner, "actives", "Lcom/artemis/utils/IntBag;");
//		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/IntBag", "getData", "()[I", false);
//		mv.visitVarInsn(ASTORE, 1);
//		Label l1 = new Label();
//		mv.visitLabel(l1);
//
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitFieldInsn(GETFIELD, owner, "flyweight", "Lcom/artemis/Entity;");
//		mv.visitVarInsn(ASTORE, 2);
//		Label l2 = new Label();
//		mv.visitLabel(l2);
//
//		mv.visitInsn(ICONST_0);
//		mv.visitVarInsn(ISTORE, 3);
//		Label l3 = new Label();
//		mv.visitLabel(l3);
//
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitFieldInsn(GETFIELD, owner, "actives", "Lcom/artemis/utils/IntBag;");
//		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/IntBag", "size", "()I", false);
//		mv.visitVarInsn(ISTORE, 4);
//		Label l4 = new Label();
//		mv.visitLabel(l4);
//
//		Label l5 = new Label();
//		mv.visitJumpInsn(GOTO, l5);
//
//		Label l6 = new Label();
//		mv.visitLabel(l6);
//
//		mv.visitFrame(Opcodes.F_FULL, 5, new Object[] {
//				owner,
//				"[I", "com/artemis/Entity",
//				Opcodes.INTEGER, Opcodes.INTEGER},
//				0, new Object[] {});
//		mv.visitVarInsn(ALOAD, 2);
//		mv.visitVarInsn(ALOAD, 1);
//		mv.visitVarInsn(ILOAD, 3);
//		mv.visitInsn(IALOAD);
//		mv.visitFieldInsn(PUTFIELD, "com/artemis/Entity", "id", "I");
//		Label l7 = new Label();
//		mv.visitLabel(l7);
//
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitVarInsn(ALOAD, 2);
//		mv.visitMethodInsn(invocation(meta.sysetemOptimizable),
//				owner, "process", "(Lcom/artemis/Entity;)V", false);
//		Label l8 = new Label();
//		mv.visitLabel(l8);
//
//		mv.visitIincInsn(3, 1);
//		mv.visitLabel(l5);
//		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//		mv.visitVarInsn(ILOAD, 4);
//		mv.visitVarInsn(ILOAD, 3);
//		mv.visitJumpInsn(IF_ICMPGT, l6);
//		Label l9 = new Label();
//		mv.visitLabel(l9);
//
//		mv.visitInsn(RETURN);
//		Label l10 = new Label();
//		mv.visitLabel(l10);
//
//		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l10, 0);
//		mv.visitLocalVariable("array", "[I", null, l1, l10, 1);
//		mv.visitLocalVariable("e", "Lcom/artemis/Entity;", null, l2, l10, 2);
//		mv.visitLocalVariable("i", "I", null, l3, l9, 3);
//		mv.visitLocalVariable("s", "I", null, l4, l9, 4);
//		mv.visitEnd();
	}

	private static int invocation(OptimizationType systemOptimization) {
		switch (systemOptimization) {
		case FULL:
			return INVOKESPECIAL; 
		case SAFE:
			return INVOKEVIRTUAL;
		case NOT_OPTIMIZABLE:
			assert false;
		default:
			throw new RuntimeException("Missing case: " + systemOptimization);
		
		}
	}
}
