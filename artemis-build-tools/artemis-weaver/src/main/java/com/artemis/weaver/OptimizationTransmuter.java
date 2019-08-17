package com.artemis.weaver;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.weaver.optimizer.EntitySystemType;
import com.artemis.weaver.optimizer.OptimizingSystemWeaver;
import com.artemis.weaver.transplant.ClassMethodTransplantAdapter;
import org.objectweb.asm.*;

public class OptimizationTransmuter extends CallableTransmuter<Void> implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;

	public OptimizationTransmuter(String file, ClassReader cr, ClassMetadata meta) {
		super(file);
		this.cr = cr;
		this.meta = meta;
	}

	@Override
	protected Void process(String file) {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;

		cv = new ClassVisitor(ASM5, cv) {
			@Override
			public MethodVisitor visitMethod(int access,
			                                 String name,
			                                 String desc,
			                                 String signature,
			                                 String[] exceptions) {


				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

				// method is injected by the transplant adapter below
				if ("processSystem".equals(name) && "()V".equals(desc))
					mv = new ProcessInvocationOptimizer(meta, mv);

				return mv;
			}
		};
		cv = new ClassMethodTransplantAdapter(
			sourceType(meta), cv, meta).addMethod("processSystem", "()V");
		cv = new OptimizingSystemWeaver(cv, meta);

		try {
			cr.accept(cv, ClassReader.EXPAND_FRAMES);
			if (file != null) ClassUtil.writeClass(cw, file);
		} catch (Exception e) {
			throw new WeaverException(e);
		}

		return null;
	}

	private static Class<?> sourceType(ClassMetadata meta) {
		switch (EntitySystemType.resolve(meta)) {
			case ENTITY_PROCESSING:
				return EntityProcessingSystem.class;
			case ITERATING:
				return IteratingSystem.class;
			default:
				throw new RuntimeException("missing case: " + EntitySystemType.resolve(meta));
		}
	}

	public ClassWriter getClassWriter() {
		return cw;
	}

	static class ProcessInvocationOptimizer extends MethodVisitor {
		private final ClassMetadata meta;

		public ProcessInvocationOptimizer(ClassMetadata meta, MethodVisitor mv) {
			super(ASM5, mv);
			this.meta = meta;
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			if ("process".equals(name) && "(I)V".equals(desc) && !itf) {
				mv.visitMethodInsn(invocation(meta.sysetemOptimizable),
					owner, name, desc, false);
			} else if ("process".equals(name) && "(Lcom/artemis/Entity;)V".equals(desc) && !itf) {
				mv.visitMethodInsn(invocation(meta.sysetemOptimizable),
					owner, name, desc, false);
			} else {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}


		private static int invocation(ClassMetadata.OptimizationType systemOptimization) {
			switch (systemOptimization) {
				case FULL:
					return INVOKESPECIAL;
				case SAFE:
					return INVOKEVIRTUAL;
				case NOT_OPTIMIZABLE:
					assert false;
				default:
					throw new RuntimeException("Missing case: " + systemOptimization);

			}
		}
	}
}