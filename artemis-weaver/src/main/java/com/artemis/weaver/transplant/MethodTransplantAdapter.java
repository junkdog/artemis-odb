package com.artemis.weaver.transplant;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.MethodDescriptor;
import com.artemis.weaver.WeaverException;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

public class MethodTransplantAdapter extends ClassVisitor implements Opcodes {
	protected final ClassReader source;
	private final ClassTransplantVisitor transplanter;

	public MethodTransplantAdapter(ClassReader source,
	                               String method,
	                               String methodDesc,
	                               ClassVisitor target,
	                               ClassMetadata meta) {
		super(ASM5, target);

		this.source = source;
		transplanter = new ClassTransplantVisitor(source, this, meta);
		transplanter.addMethod(new MethodDescriptor(method, methodDesc));
	}

	public MethodTransplantAdapter(Class<?> source,
	                               String method,
	                               String methodDesc,
	                               ClassVisitor target,
	                               ClassMetadata meta) {

		this(toClassReader(source), method, methodDesc, target, meta);
	}

	@Override
	public void visitEnd() {
		source.accept(transplanter, 0);
		super.visitEnd();
	}

	private static ClassReader toClassReader(Class<?> klazz) {
		try {
			String resourceName = "/" + klazz.getName().replace('.', '/') + ".class";
			InputStream classStream = klazz.getResourceAsStream(resourceName);
			return new ClassReader(classStream);
		} catch (IOException e) {
			throw new WeaverException("Failed to create reader for " + klazz, e);
		}
	}

}
