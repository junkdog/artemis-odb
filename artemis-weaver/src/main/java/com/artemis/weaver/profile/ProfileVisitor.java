package com.artemis.weaver.profile;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;

public class ProfileVisitor extends ClassVisitor implements Opcodes {
	private ClassMetadata info;
	
	public ProfileVisitor(ClassVisitor cv, ClassMetadata info) {
		super(ASM4, cv);
		this.info = info;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature,
		String[] exceptions) {
		MethodVisitor method = super.visitMethod(access, name, desc, signature, exceptions);
		
		if ("begin".equals(name) && "()V".equals(desc))
			method = new ProfileBeginWeaver(method, info, access, name, desc);
		else if ("end".equals(name) && "()V".equals(desc))
			method = new ProfileEndWeaver(method, info, access, name, desc);
		else if ("initialize".equals(name) && "()V".equals(desc))
			method = new ProfileInitializeWeaver(method, info, access, name, desc);
		
		return method;
	}
	
	@Override
	public void visitEnd() {
		super.visitEnd();
	}
}