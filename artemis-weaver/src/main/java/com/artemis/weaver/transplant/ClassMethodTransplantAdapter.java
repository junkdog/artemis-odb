package com.artemis.weaver.transplant;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.MethodDescriptor;
import org.objectweb.asm.*;

public class ClassMethodTransplantAdapter extends ClassVisitor implements Opcodes {
	protected final ClassReader source;
	private final ClassMethodTransplantVisitor transplanter;

	public ClassMethodTransplantAdapter(ClassReader source, ClassVisitor target, ClassMetadata meta) {
		super(ASM5, target);
		this.source = source;
		transplanter = new ClassMethodTransplantVisitor(source, this, meta);
	}

	public ClassMethodTransplantAdapter(Class<?> source, ClassVisitor target, ClassMetadata meta) {
		this(Weaver.toClassReader(source), target, meta);
	}

	public ClassMethodTransplantAdapter addMethod(String method, String methodDesc) {
		transplanter.addMethod(new MethodDescriptor(method, methodDesc));
		return this;
	}

	@Override
	public void visitEnd() {
		source.accept(transplanter, 0);
		super.visitEnd();
	}
}
