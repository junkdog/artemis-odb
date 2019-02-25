package com.artemis.weaver;

import com.artemis.ClassUtil;
import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.FieldDescriptor;
import com.artemis.weaver.transplant.ClassTransplantVisitor;
import com.artemis.weaver.transplant.MethodBodyTransplanter;
import org.objectweb.asm.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityLinkGenerator extends CallableTransmuter<Void> implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;

	public EntityLinkGenerator(String file, ClassReader cr, ClassMetadata meta) {
		super(file);
		this.cr = cr;
		this.meta = meta;
	}
	
	@Override
	protected Void process(String file) throws IOException {
		final List<FieldDescriptor> mutators = new ArrayList<FieldDescriptor>();
		for (FieldDescriptor fd : meta.fields()) {
			if (fd.entityLinkMutator != null) {
				String mutatorFile = file.replaceAll("\\.class", "\\$Mutator_" + fd.name + ".class");
				generateMutator(fd, mutatorFile);
				mutators.add(fd);
			}
		}
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;
		cv = new ClassVisitor(ASM5, cv) {
			boolean injectedMutatorsReferences;

			private void injectMutators() {
				injectedMutatorsReferences = true;

				String internalName = meta.type.getInternalName();
				for (FieldDescriptor mutator : mutators) {
					super.visitInnerClass(
						internalName + getMutatorName(mutator),
						internalName,
						getMutatorName(mutator),
						ACC_PUBLIC | ACC_STATIC);
				}
			}

			@Override
			public void visitInnerClass(String name, String outerName, String innerName, int access) {
				super.visitInnerClass(name, outerName, innerName, access);
				injectMutators();
			}

			@Override
			public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
				if (!injectedMutatorsReferences)
					injectMutators();

				return super.visitField(access, name, desc, signature, value);
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				if (!injectedMutatorsReferences)
					injectMutators();

				return super.visitMethod(access, name, desc, signature, exceptions);
			}

			@Override
			public void visitEnd() {
				if (!injectedMutatorsReferences)
					injectMutators();

				super.visitEnd();
			}
		};

		try {
			cr.accept(cv, ClassReader.EXPAND_FRAMES);
			if (file != null) ClassUtil.writeClass(cw, file);
		} catch (Exception e) {
			throw new WeaverException(e);
		}
		
		return null;
	}

	private void generateMutator(final FieldDescriptor fd, final String file) {
		final ClassReader sourceClassReader = Weaver.toClassReader(fd.entityLinkMutator);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;
		final String className = meta.type.getInternalName() + getMutatorName(fd);
		cv = new ClassVisitor(ASM5, cv) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				Class<?> enclosingClass = fd.entityLinkMutator.getEnclosingClass();
				return new MethodBodyTransplanter(enclosingClass, meta.type, mv);
			}
		};
		cv = new ClassVisitor(ASM5, cv) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				return new MethodBodyTransplanter(fd.entityLinkMutator, Type.getObjectType(className), mv);
			}
		};
		cv = new ClassVisitor(ASM5, cv) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
				Type outerClass = Type.getType(fd.entityLinkMutator.getDeclaringClass());
				String originalOuter = outerClass.getInternalName();
				if (signature != null && signature.contains(originalOuter)) {
					String newOuter = meta.type.getInternalName();
					signature = signature.replaceAll(originalOuter, newOuter);
				}

				super.visit(version, access, name, signature, superName, interfaces);
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				final Type outerClass = Type.getType(fd.entityLinkMutator.getDeclaringClass());
				final String originalOuter = outerClass.getInternalName();
				if (desc.contains(originalOuter)) {
					desc = desc.replaceAll(originalOuter, meta.type.getInternalName());
				}
				if (signature != null && signature.contains(originalOuter)) {
					signature = signature.replaceAll(originalOuter, meta.type.getInternalName());
				}


				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

				return new MethodVisitor(ASM5, mv) {
					@Override
					public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
						if (owner != null && owner.contains(originalOuter)) {
							owner = owner.replaceAll(originalOuter, meta.type.getInternalName());
						}
						if (desc != null && desc.contains(originalOuter)) {
							desc = desc.replaceAll(originalOuter, meta.type.getInternalName());
						}
						super.visitMethodInsn(opcode, owner, name, desc, itf);
					}

					@Override
					public void visitFieldInsn(int opcode, String owner, String name, String desc) {
						if ("field".equals(name)) // see template classes
							name = fd.name;

						super.visitFieldInsn(opcode, owner, name, desc);
					}
				};
			}
		};
		cv = new ClassVisitor(ASM5, cv) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
				Type target = Type.getObjectType(meta.type.getInternalName() + "$Mutator_" + fd.name);
				return new MethodBodyTransplanter(sourceClassReader.getClassName(), target, mv);
			}
		};
		cv = new ClassTransplantVisitor(sourceClassReader, cv, Weaver.scan(fd.entityLinkMutator), className);

		try {
			sourceClassReader.accept(cv, ClassReader.EXPAND_FRAMES);
			if (file != null)
				ClassUtil.writeClass(cw, file);

		} catch (Exception e) {
			e.printStackTrace();
			throw new WeaverException(e);
		}
	}

	static String getMutatorName(FieldDescriptor fd) {
		return "$Mutator_" + fd.name;
	}
}
