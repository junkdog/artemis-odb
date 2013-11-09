package com.artemis.weaver.packed;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.TypedOpcodes;

public class StaticInitializerVisitor extends MethodVisitor implements Opcodes {

	private final ClassMetadata meta;
	private final TypedOpcodes opcodes;

	public StaticInitializerVisitor(MethodVisitor mv, ClassMetadata meta) {
		super(ASM4, mv);
		this.meta = meta;
		opcodes = new TypedOpcodes(meta);
	}
	
	@Override
	public void visitCode() {
		if (instanceFields(meta).size() == 0)
			return;
		
		mv.visitCode();
		mv.visitIntInsn(SIPUSH, (64 * instanceFields(meta).size()));
		mv.visitIntInsn(NEWARRAY, opcodes.newArrayType());
		mv.visitFieldInsn(PUTSTATIC, meta.type.getInternalName(), "$data", arrayDesc());
	}

	private String arrayDesc() {
		return "[" + instanceFields(meta).get(1).desc;
	}
}
