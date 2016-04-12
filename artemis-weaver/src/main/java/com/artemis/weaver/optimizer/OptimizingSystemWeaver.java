package com.artemis.weaver.optimizer;


import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.OptimizationType;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class OptimizingSystemWeaver extends ClassVisitor implements Opcodes {

	private final ClassVisitor cv;
	private final ClassMetadata meta;

	 public OptimizingSystemWeaver(ClassVisitor cv, ClassMetadata meta) {
		super(ASM4, cv);
		this.cv = cv;
		this.meta = meta;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		String systemSuperName = EntitySystemType.resolve(meta).replacedSuperName;
		cv.visit(version, access, name, signature, systemSuperName, interfaces);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		
		if (isProcessMethod(name, desc)) {
			access = meta.sysetemOptimizable == OptimizationType.FULL ? ACC_PRIVATE : access;
		}
		
		MethodVisitor method = cv.visitMethod(access, name, desc, signature, exceptions);
		method = new SystemMethodVisitor(method, meta);
		
		return method;
		
	}

	private boolean isProcessMethod(String name, String desc) {
		return "process".equals(name) &&
			("(I)V".equals(desc) || "(Lcom/artemis/Entity;)V".equals(desc));
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return (Weaver.POOLED_ANNOTATION.equals(desc))
			? null
			: super.visitAnnotation(desc, visible);
	}
}
