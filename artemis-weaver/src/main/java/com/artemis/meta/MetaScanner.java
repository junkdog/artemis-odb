package com.artemis.meta;


import static com.artemis.Weaver.PACKED_ANNOTATION;
import static com.artemis.Weaver.POOLED_ANNOTATION;
import static com.artemis.Weaver.WOVEN_ANNOTATION;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata.WeaverType;

public class MetaScanner extends ClassVisitor implements Opcodes {
	
	private static final class AnnotationReader extends AnnotationVisitor {
		
		private ClassMetadata info;

		private AnnotationReader(AnnotationVisitor av, ClassMetadata meta) {
			super(ASM4, av);
			this.info = meta;
		}

		@Override
		public void visit(String name, Object value) {
			if ("forceWeaving".equals(name)) {
				info.forcePooledWeaving = (Boolean)value;
			}
			super.visit(name, value);
		}
	}

	private ClassMetadata info;

	public MetaScanner(ClassMetadata metadata) {
		super(ASM4);
		info = metadata;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		info.superClass = superName;
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationVisitor av = super.visitAnnotation(desc, visible);
		
		if (PACKED_ANNOTATION.equals(desc)) {
			info.annotation = WeaverType.PACKED;
		} else if (POOLED_ANNOTATION.equals(desc)) {
			info.annotation = WeaverType.POOLED;
			av = new AnnotationReader(av, info);
		} else if (WOVEN_ANNOTATION.equals(desc)) {
			info.isPreviouslyProcessed = true;
		}
		
		return av;
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		info.fields.add(new FieldDescriptor(access, name, desc, signature, value));
		return super.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ("reset".equals(name) && "()V".equals(desc))
			info.foundReset = true;
		else if ("forEntity".equals(name) && desc.startsWith("(Lcom/artemis/Entity;)"))
			info.foundEntityFor = true;
		else if ("begin".equals(name) && desc.equals("()V"))
			info.foundBegin = true;
		else if ("end".equals(name) && desc.equals("()V"))
			info.foundEnd = true;
		else if ("<clinit>".equals(name) && "()V".equals(desc))
			info.foundStaticInitializer = true;
		
		info.methods.add(new MethodDescriptor(access, name, desc, signature, exceptions));
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}