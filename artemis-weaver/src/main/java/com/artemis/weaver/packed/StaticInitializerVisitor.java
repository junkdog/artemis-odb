package com.artemis.weaver.packed;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;

public class StaticInitializerVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;

	public StaticInitializerVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM4, mv);
		this.meta = meta;
	}
	
	@Override
	public void visitCode() {
		if (instanceFields(meta).size() == 0)
			return;
		
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitIntInsn(SIPUSH, ClassMetadataUtil.sizeOf(meta) * 128);
		mv.visitMethodInsn(INVOKESTATIC, "java/nio/ByteBuffer", "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
		mv.visitInsn(RETURN);
	}
}
