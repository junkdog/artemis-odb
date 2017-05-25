package com.artemis.meta;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class ProfileAnnotationReader extends AnnotationVisitor {
	private final String annotationField;
	private ClassMetadata info;

	ProfileAnnotationReader(String name, ClassMetadata info) {
		super(Opcodes.ASM4);
		this.annotationField = name;
		this.info = info;
	}

	@Override
	public void visit(String field, Object value) {
		if ("using".equals(field))
			info.profilerClass = (Type)value;
		else if ("enabled".equals(field))
			info.profilingEnabled = (Boolean)value;
		
		super.visit(annotationField, value);
	}
	
	@Override
	public AnnotationVisitor visitArray(final String name) {
		return new ProfileAnnotationReader(name, info);
	}
}