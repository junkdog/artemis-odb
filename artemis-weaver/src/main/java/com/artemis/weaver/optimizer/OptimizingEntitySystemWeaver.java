package com.artemis.weaver.optimizer;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.OptimizationType;

public class OptimizingEntitySystemWeaver extends ClassVisitor implements Opcodes {

	private final ClassMetadata meta;
	
	public OptimizingEntitySystemWeaver(ClassVisitor cv, ClassMetadata meta) {
		super(ASM4, cv);
		this.meta = meta;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, "com/artemis/EntitySystem", interfaces);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		
		MethodVisitor method = null;
		if ("process".equals(name) && "(Lcom/artemis/Entity;)V".equals(desc)) {
			access = meta.sysetemOptimizable == OptimizationType.FULL ? ACC_PRIVATE : access;
		}
		
		method = cv.visitMethod(access, name, desc, signature, exceptions);
		method = new EsMethodVisitor(method, meta);
		
		return method;
		
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return (Weaver.PACKED_ANNOTATION.equals(desc) || Weaver.POOLED_ANNOTATION.equals(desc))
			? null
			: super.visitAnnotation(desc, visible);
	}
}
