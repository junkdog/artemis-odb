package com.artemis.model.scan;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ArtemisMetaScanner extends ClassVisitor {
	
	static final String SYSTEM_ANNOTATION = "Llombok/ArtemisSystem;";
	static final String MANAGER_ANNOTATION = "Llombok/ArtemisManager;";
	static final String INJECTED_ANNOTATION = "Llombok/ArtemisInjected;";
	private ArtemisConfigurationData info;

	ArtemisMetaScanner(ArtemisConfigurationData config) {
		super(Opcodes.ASM4);
		this.info = config;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (SYSTEM_ANNOTATION.equals(desc))
			return new ArtemisAnnotationReader(desc, info);
		else if (MANAGER_ANNOTATION.equals(desc))
			return new ArtemisAnnotationReader(desc, info);
		else if (INJECTED_ANNOTATION.equals(desc))
			return new ArtemisAnnotationReader(desc, info);
			
		return super.visitAnnotation(desc, visible);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}