package com.artemis.meta;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata.WeaverType;

public class MetaScanner extends ClassVisitor {
	static final String PACKED_ANNOTATION = "Lcom/artemis/annotations/PackedWeaver;";
	static final String POOLED_ANNOTATION = "Lcom/artemis/annotations/PooledWeaver;";
	static final String WOVEN_ANNOTATION = "Lcom/artemis/annotations/internal/Transmuted";

	private ClassMetadata info;

	public MetaScanner(ClassMetadata annotationMirror) {
		super(Opcodes.ASM4);
		info = annotationMirror;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (PACKED_ANNOTATION.equals(desc))
			info.annotation = WeaverType.PACKED;
		else if (POOLED_ANNOTATION.equals(desc))
			info.annotation = WeaverType.POOLED;
		else if (WOVEN_ANNOTATION.equals(desc))
			info.isPreviouslyProcessed = true;
			
		return super.visitAnnotation(desc, visible);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ("reset".equals(name) && "()V".equals(desc))
			info.foundReset = true;
		else if ("forEntity".equals(name) && "()V".equals(desc))
			info.foundEntityFor = true;
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}