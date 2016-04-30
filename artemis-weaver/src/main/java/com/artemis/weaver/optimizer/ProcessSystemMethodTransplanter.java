package com.artemis.weaver.optimizer;

import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.WeaverException;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

public class ProcessSystemMethodTransplanter extends ClassVisitor implements Opcodes {
	private final ClassReader source;
	private final String method;
	private final String methodDesc;
	private final ClassMetadata meta;

	public ProcessSystemMethodTransplanter(ClassReader source,
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

	public ProcessSystemMethodTransplanter(Class<?> source,
	                                       String method,
	                                       String methodDesc,
	                                       ClassVisitor target,
	                                       ClassMetadata meta) {

		this(toClassReader(source), method, methodDesc, target, meta);
	}

	@Override
	public void visitEnd() {
		source.accept(new ClassVisitor(ASM5) {
			@Override
			public MethodVisitor visitMethod(int access,
			                                 String name,
			                                 String desc,
			                                 String signature,
			                                 String[] exceptions) {

				if (!(method.equals(name) && methodDesc.equals(desc)))
					return null;

				MethodVisitor mv = ProcessSystemMethodTransplanter.this.visitMethod(access, name, desc, signature, exceptions);
				return new BodyTransplantAdapter(source.getClassName(), meta, mv);
			}
		}, 0);


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

	protected static class BodyTransplantAdapter extends MethodVisitor {
		private final String owner;
		private final String oldOwner;
		private final ClassMetadata meta;

		public BodyTransplantAdapter(String oldOwner, ClassMetadata meta, MethodVisitor mv) {
			super(ASM5, mv);
			this.oldOwner = oldOwner;
			this.meta = meta;
			this.owner = meta.type.getInternalName();
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			// remove line numbers
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if (oldOwner.equals(owner))
				owner = this.owner;

			super.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			if (local.length > 0 && oldOwner.equals(local[0]))
				local[0] = owner;

			super.visitFrame(type, nLocal, local, nStack, stack);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			if (oldOwner.equals(owner))
				owner = this.owner;

			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			if ("this".equals(name))
				desc = meta.type.toString();

			super.visitLocalVariable(name, desc, signature, start, end, index);
		}
	}
}
