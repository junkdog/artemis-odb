package com.artemis.weaver.transplant;

import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.WeaverException;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

public class MethodTransplantAdapter extends ClassVisitor implements Opcodes {
	protected final ClassReader source;
	protected final String method;
	protected final String methodDesc;
	protected final ClassMetadata meta;

	public MethodTransplantAdapter(ClassReader source,
	                               String method,
	                               String methodDesc,
	                               ClassVisitor target,
	                               ClassMetadata meta) {
		super(ASM5, target);
		this.source = source;
		this.method = method;
		this.methodDesc = methodDesc;
		this.meta = meta;
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
		SourceTransplantVisitor visitor =
			new SourceTransplantVisitor(source, method, methodDesc, this, meta);

		source.accept(visitor, 0);
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

	static class SourceTransplantVisitor extends ClassVisitor {
		private final ClassVisitor target;
		private final ClassMetadata meta;
		private final String method;
		private final String methodDesc;
		private final ClassReader source;

		public SourceTransplantVisitor(ClassReader source, String method, String methodDesc, ClassVisitor target, ClassMetadata meta) {
			super(Opcodes.ASM5);
			this.target = target;
			this.meta = meta;
			this.method = method;
			this.methodDesc = methodDesc;
			this.source = source;
		}

		@Override
		public MethodVisitor visitMethod(int access,
		                                 String name,
		                                 String desc,
		                                 String signature,
		                                 String[] exceptions) {

			if (!(method.equals(name) && methodDesc.equals(desc)))
				return null;

			MethodVisitor mv = target.visitMethod(access, name, desc, signature, exceptions);
			return new MethodBodyTransplanter(source.getClassName(), meta, mv);
		}
	}
}
