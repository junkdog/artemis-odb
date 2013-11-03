package com.artemis.weaver;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;

public class StaticInitializerVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;

	public StaticInitializerVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM4, mv);
		this.meta = meta;
	}
	
	@Override
	public void visitCode() {
		mv.visitCode();
		mv.visitIntInsn(BIPUSH, 64);
		mv.visitIntInsn(NEWARRAY, T_FLOAT);
		mv.visitFieldInsn(PUTSTATIC, meta.type.getInternalName(), "$data", arrayDesc());
	}

	private String arrayDesc() {
		return "[" + instanceFields(meta).get(1).desc;
	}
}
