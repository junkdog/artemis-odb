package com.artemis.weaver.optimizer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;

public final class ProcessEntitiesInjector implements Opcodes {

	private final ClassReader cr;
	private final ClassMetadata meta;
	private final ClassWriter cw;

	public ProcessEntitiesInjector(ClassReader cr, ClassMetadata meta) {
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cr = cr;
		this.meta = meta;
	}

	public ClassReader transform() {
		return new ClassReader(injectMethods());
	}

	private byte[] injectMethods() {
		injectProcessEntities();
		cr.accept(cw, 0);
		return cw.toByteArray();
	}
	
	private void injectProcessEntities() {
		String owner = meta.type.getInternalName();
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED + ACC_FINAL, "processEntities", "(Lcom/artemis/utils/ImmutableBag;)V", "(Lcom/artemis/utils/ImmutableBag<Lcom/artemis/Entity;>;)V", null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);

		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, "com/artemis/utils/Bag");
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/Bag", "getData", "()[Ljava/lang/Object;");
		mv.visitVarInsn(ASTORE, 2);
		Label l1 = new Label();
		mv.visitLabel(l1);

		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 3);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "com/artemis/utils/ImmutableBag", "size", "()I");
		mv.visitVarInsn(ISTORE, 4);
		Label l3 = new Label();
		mv.visitLabel(l3);
		Label l4 = new Label();
		mv.visitJumpInsn(GOTO, l4);
		Label l5 = new Label();
		mv.visitLabel(l5);

		mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {"[Ljava/lang/Object;", Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ILOAD, 3);
		mv.visitInsn(AALOAD);
		mv.visitTypeInsn(CHECKCAST, "com/artemis/Entity");
		mv.visitMethodInsn(INVOKEVIRTUAL, owner, "process", "(Lcom/artemis/Entity;)V");
		Label l6 = new Label();
		mv.visitLabel(l6);

		mv.visitIincInsn(3, 1);
		mv.visitLabel(l4);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitVarInsn(ILOAD, 3);
		mv.visitJumpInsn(IF_ICMPGT, l5);
		Label l7 = new Label();
		mv.visitLabel(l7);

		mv.visitInsn(RETURN);
		Label l8 = new Label();
		mv.visitLabel(l8);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l8, 0);
		mv.visitLocalVariable("entities", "Lcom/artemis/utils/ImmutableBag;", "Lcom/artemis/utils/ImmutableBag<Lcom/artemis/Entity;>;", l0, l8, 1);
		mv.visitLocalVariable("array", "[Ljava/lang/Object;", null, l1, l8, 2);
		mv.visitLocalVariable("i", "I", null, l2, l7, 3);
		mv.visitLocalVariable("s", "I", null, l3, l7, 4);
		mv.visitEnd();
	}
}
