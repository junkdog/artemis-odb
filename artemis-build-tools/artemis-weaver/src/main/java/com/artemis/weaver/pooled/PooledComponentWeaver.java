package com.artemis.weaver.pooled;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.ConstructorInvocationVisitor;

public class PooledComponentWeaver extends ClassVisitor implements Opcodes{

	private ClassMetadata meta;
	
	public PooledComponentWeaver(ClassVisitor cv, ClassMetadata meta) {
		super(ASM4, cv);
		this.meta = meta;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor method = cv.visitMethod(access, name, desc, signature, exceptions);
		
		if ("<init>".equals(name))
			method = new ConstructorInvocationVisitor(method, meta);
		if ("reset".equals(name) && "()V".equals(desc))
			method = new ResetMethodVisitor(method, meta);
		
		return method;
	}
}
