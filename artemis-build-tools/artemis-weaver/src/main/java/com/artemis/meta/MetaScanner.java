package com.artemis.meta;


import static com.artemis.Weaver.POOLED_ANNOTATION;
import static com.artemis.Weaver.PROFILER_ANNOTATION;
import static com.artemis.Weaver.WOVEN_ANNOTATION;

import com.artemis.weaver.optimizer.EntitySystemType;
import com.artemis.weaver.template.MultiEntityIdLink;
import com.artemis.weaver.template.MultiEntityLink;
import com.artemis.weaver.template.UniEntityIdLink;
import com.artemis.weaver.template.UniEntityLink;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata.OptimizationType;
import com.artemis.meta.ClassMetadata.WeaverType;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

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
		super(ASM8);
		info = metadata;
	}

	@Override
	public void visit(int version,
	                  int access,
	                  String name,
	                  String signature,
	                  String superName,
	                  String[] interfaces) {

		info.superClass = superName;
		if (EntitySystemType.resolve(info) != null)
			info.sysetemOptimizable = OptimizationType.FULL;
		
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		AnnotationVisitor av = super.visitAnnotation(desc, visible);
		
		if (POOLED_ANNOTATION.equals(desc)) {
			info.annotation = WeaverType.POOLED;
			av = new AnnotationReader(av, info);
		} else if (PROFILER_ANNOTATION.equals(desc)) {
			return new ProfileAnnotationReader(desc, info);
		} else if (WOVEN_ANNOTATION.equals(desc)) {
			info.isPreviouslyProcessed = true;
		} else if (info.sysetemOptimizable == OptimizationType.FULL
				&& Weaver.PRESERVE_VISIBILITY_ANNOTATION.equals(desc)) {
			
			info.sysetemOptimizable = OptimizationType.SAFE;
		}
		
		return av;
	}
	
	@Override
	public FieldVisitor visitField(int access,
	                               String name,
	                               String desc,
	                               String signature,
	                               Object value) {

		final FieldDescriptor field = info.field(name);
		field.set(access, desc, signature, value);

		FieldVisitor fv = super.visitField(access, name, desc, signature, value);

		if ("Lcom/artemis/Entity;".equals(desc)) {
			field.entityLinkMutator = UniEntityLink.Mutator.class;
		} else if ("I".equals(desc)) {
			fv = new EntityIdScanVisitor(fv, field, UniEntityIdLink.Mutator.class);
		} else if ("Lcom/artemis/utils/IntBag;".equals(desc)) {
			fv = new EntityIdScanVisitor(fv, field, MultiEntityIdLink.Mutator.class);
		} else if ("Lcom/artemis/utils/Bag<Lcom/artemis/Entity;>;".equals(signature)) {
			field.entityLinkMutator = MultiEntityLink.Mutator.class;
		}

		if (field.isResettable())
			field.reset = constInstructionFor(field);

		return fv;
	}
	
	@Override
	public MethodVisitor visitMethod(int access,
	                                 String name,
	                                 String desc,
	                                 String signature,
	                                 String[] exceptions) {

		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

		info.methods.add(new MethodDescriptor(access, name, desc, signature, exceptions));
		if ("reset".equals(name) && "()V".equals(desc))
			info.foundReset = true;
		else if ("begin".equals(name) && "()V".equals(desc))
			info.foundBegin = true;
		else if ("end".equals(name) && desc.equals("()V"))
			info.foundEnd = true;
		else if ("initialize".equals(name) && "()V".equals(desc))
			info.foundInitialize = true;

		if ("<init>".equals(name) && "()V".equals(desc)) {
			return new DefaultValueScanner(mv, info);
		} else {
			return mv;
		}
	}

	private static AbstractInsnNode constInstructionFor(FieldDescriptor field) {
		if ("Ljava/lang/String;".equals(field.desc))
				return new InsnNode(ACONST_NULL);

		switch (field.desc.charAt(0)) {
			case 'Z':
			case 'B':
			case 'C':
			case 'S':
			case 'I':
				return new InsnNode(ICONST_0);
			case 'J':
				return new InsnNode(LCONST_0);
			case 'F':
				return new InsnNode(FCONST_0);
			case 'D':
				return new InsnNode(DCONST_0);
		}

		throw new RuntimeException(field.toString());
	}

	private static class EntityIdScanVisitor extends FieldVisitor {
		private final FieldDescriptor field;
		private final Class<?> mutatorClass;

		public EntityIdScanVisitor(FieldVisitor fv,
		                           FieldDescriptor field,
		                           Class<?> mutatorClassIfFound) {

			super(Opcodes.ASM5, fv);
			this.field = field;
			this.mutatorClass = mutatorClassIfFound;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			if ("Lcom/artemis/annotations/EntityId;".equals(desc)) {
				field.entityLinkMutator = mutatorClass;
			}

			return super.visitAnnotation(desc, visible);
		}
	}
}