package com.artemis.weaver.packed;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;
import com.artemis.weaver.TypedOpcodes;

public class PackedStubs implements Opcodes {
	
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;

	public PackedStubs(ClassReader cr, ClassMetadata meta) {
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cr = cr;
		this.meta = meta;
	}
	
	public ClassReader transform() {
		return new ClassReader(injectPackedComponentStubs());
	}
	
	private byte[] injectPackedComponentStubs() {
		List<FieldDescriptor> dataFields = instanceFields(meta);
		if (!meta.foundStaticInitializer && dataFields.size()  > 0)
			injectStaticInitializer();
		
		if (!meta.foundEntityFor)
			injectForEntity();
		injectEnsureCapacity();
		
		if (dataFields.size() > 0) {
			
			injectConstructor();
		
			cw.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "$_SIZE_OF", "I", null,
				Integer.valueOf(ClassMetadataUtil.sizeOf(meta))).visitEnd();;
			cw.visitField(ACC_PRIVATE, "$stride", "I", null, Integer.valueOf(0)).visitEnd();
			cw.visitField(ACC_PRIVATE + ACC_STATIC, "$store", "Ljava/util/Map;",
					mapSignature(), null).visitEnd();;
		}
		
		// Reason for recreating teh ClassReader/Writer combo:
		// To make life simpler, the reset method acts on the original fields, 
		// delegating the ByteBuffer weaving to the FieldToArrayClassTransformer.
		injectReset();
		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		
		if (dataFields.size() > 0) {
			cw.visitField(ACC_PRIVATE, "$world", "Lcom/artemis/World;", null, null).visitEnd();
			cw.visitField(ACC_PRIVATE, "$data", "Ljava/nio/ByteBuffer;", null, null).visitEnd();
			injectGrow(meta.type.getInternalName());
			injectDispose(meta.type.getInternalName());
			
			FieldToStructTransformer transformer = new FieldToStructTransformer(meta);
			ClassNode cn = transformer.transform(cr);
			
			cn.accept(cw);
		} else {
			cr.accept(cw, 0);
		}
		return cw.toByteArray();
	}

	private String mapSignature() {
		return "Ljava/util/Map<Lcom/artemis/World;Lcom/artemis/utils/Bag<"
				+ meta.type.getDescriptor() + ">;>;";
	}
	
	private void injectDispose(String owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "free", "(Lcom/artemis/World;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitFieldInsn(GETSTATIC, owner, "$store", "Ljava/util/Map;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(POP);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l2, 0);
		mv.visitLocalVariable("world", "Lcom/artemis/World;", null, l0, l2, 1);
		mv.visitEnd();
	}

	private void injectGrow(String owner) {
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "$grow", "(I)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitMethodInsn(INVOKESTATIC, "java/nio/ByteBuffer", "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
		mv.visitVarInsn(ASTORE, 2);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 3);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "capacity", "()I");
		mv.visitVarInsn(ISTORE, 4);
		Label l3 = new Label();
		mv.visitLabel(l3);
		Label l4 = new Label();
		mv.visitJumpInsn(GOTO, l4);
		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {"java/nio/ByteBuffer", Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ILOAD, 3);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitVarInsn(ILOAD, 3);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "get", "(I)B");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "put", "(IB)Ljava/nio/ByteBuffer;");
		mv.visitInsn(POP);
		Label l6 = new Label();
		mv.visitLabel(l6);
		mv.visitIincInsn(3, 1);
		mv.visitLabel(l4);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitVarInsn(ILOAD, 3);
		mv.visitJumpInsn(IF_ICMPGT, l5);
		Label l7 = new Label();
		mv.visitLabel(l7);
		mv.visitFieldInsn(GETSTATIC, owner, "$store", "Ljava/util/Map;");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$world", "Lcom/artemis/World;");
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, "com/artemis/utils/Bag");
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/Bag", "iterator", "()Ljava/util/Iterator;");
		mv.visitVarInsn(ASTORE, 4);
		Label l8 = new Label();
		mv.visitJumpInsn(GOTO, l8);
		Label l9 = new Label();
		mv.visitLabel(l9);
		mv.visitFrame(Opcodes.F_FULL, 5, new Object[] {owner, Opcodes.INTEGER, "java/nio/ByteBuffer", Opcodes.TOP, "java/util/Iterator"}, 0, new Object[] {});
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, owner);
		mv.visitVarInsn(ASTORE, 3);
		Label l10 = new Label();
		mv.visitLabel(l10);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitFieldInsn(PUTFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitLabel(l8);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 4);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
		mv.visitJumpInsn(IFNE, l9);
		Label l11 = new Label();
		mv.visitLabel(l11);
		mv.visitInsn(RETURN);
		Label l12 = new Label();
		mv.visitLabel(l12);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l12, 0);
		mv.visitLocalVariable("capacity", "I", null, l0, l12, 1);
		mv.visitLocalVariable("newBuffer", "Ljava/nio/ByteBuffer;", null, l1, l12, 2);
		mv.visitLocalVariable("i", "I", null, l2, l7, 3);
		mv.visitLocalVariable("s", "I", null, l3, l7, 4);
		mv.visitLocalVariable("ref", meta.type.toString(), null, l10, l8, 3);
		mv.visitEnd();
	}

	
	private void injectStaticInitializer() {
		String owner = meta.type.getInternalName();
		
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitTypeInsn(NEW, "java/util/IdentityHashMap");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/IdentityHashMap", "<init>", "()V");
		mv.visitFieldInsn(PUTSTATIC, owner, "$store", "Ljava/util/Map;");
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}
	
	private void injectConstructor() {
		String typeName = meta.type.getInternalName();
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/artemis/World;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "com/artemis/PackedComponent", "<init>", "()V");
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ACONST_NULL);
		mv.visitFieldInsn(PUTFIELD, typeName, "$data", "Ljava/nio/ByteBuffer;");
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, typeName, "$world", "Lcom/artemis/World;");
		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitFieldInsn(GETSTATIC, typeName, "$store", "Ljava/util/Map;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, "com/artemis/utils/Bag");
		mv.visitVarInsn(ASTORE, 2);
		Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitVarInsn(ALOAD, 2);
		Label l5 = new Label();
		mv.visitJumpInsn(IFNULL, l5);
		Label l6 = new Label();
		mv.visitLabel(l6);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitInsn(ICONST_0);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/Bag", "get", "(I)Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, typeName);
		mv.visitFieldInsn(GETFIELD, typeName, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitFieldInsn(PUTFIELD, typeName, "$data", "Ljava/nio/ByteBuffer;");
		Label l7 = new Label();
		mv.visitLabel(l7);
		Label l8 = new Label();
		mv.visitJumpInsn(GOTO, l8);
		mv.visitLabel(l5);
		mv.visitFrame(Opcodes.F_FULL, 3, new Object[] {typeName, "com/artemis/World", "com/artemis/utils/Bag"}, 0, new Object[] {});
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, ClassMetadataUtil.sizeOf(meta) * 128);
		mv.visitMethodInsn(INVOKESTATIC, "java/nio/ByteBuffer", "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
		mv.visitFieldInsn(PUTFIELD, typeName, "$data", "Ljava/nio/ByteBuffer;");
		Label l9 = new Label();
		mv.visitLabel(l9);
		mv.visitTypeInsn(NEW, "com/artemis/utils/Bag");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "com/artemis/utils/Bag", "<init>", "()V");
		mv.visitVarInsn(ASTORE, 2);
		Label l10 = new Label();
		mv.visitLabel(l10);
		mv.visitFieldInsn(GETSTATIC, typeName, "$store", "Ljava/util/Map;");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		mv.visitInsn(POP);
		mv.visitLabel(l8);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/Bag", "add", "(Ljava/lang/Object;)V");
		Label l11 = new Label();
		mv.visitLabel(l11);
		mv.visitInsn(RETURN);
		Label l12 = new Label();
		mv.visitLabel(l12);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l12, 0);
		mv.visitLocalVariable("world", "Lcom/artemis/World;", null, l0, l12, 1);
		mv.visitLocalVariable("instances", "Lcom/artemis/utils/Bag;", "Lcom/artemis/utils/Bag<" + meta.type.toString() + ">;", l4, l12, 2);
		mv.visitEnd();
	}
	
	private void injectEnsureCapacity() {
		String owner = meta.type.getInternalName();
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "ensureCapacity", "(I)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		if (instanceFields(meta).size() > 0) {
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_1);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(GETSTATIC, owner, "$_SIZE_OF", "I");
			mv.visitInsn(IMUL);
			mv.visitVarInsn(ISTORE, 2);
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitLabel(l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "capacity", "()I");
			mv.visitVarInsn(ILOAD, 2);
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPGE, l2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "capacity", "()I");
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "max", "(II)I");
			mv.visitInsn(IMUL);
			mv.visitMethodInsn(INVOKESPECIAL, owner, "$grow", "(I)V");
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", meta.type.toString(), null, l0, l4, 0);
			mv.visitLocalVariable("id", "I", null, l0, l4, 1);
			mv.visitLocalVariable("requested", "I", null, l1, l4, 2);
		} else {
			mv.visitInsn(RETURN);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitLocalVariable("this", meta.type.toString(), null, l0, l4, 0);
			mv.visitLocalVariable("id", "I", null, l0, l4, 1);
		}
		mv.visitEnd();
	}
	
	private void injectForEntity() {
		String owner = meta.type.getInternalName();
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "forEntity", "(Lcom/artemis/Entity;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		if (instanceFields(meta).size() > 0) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETSTATIC, owner, "$_SIZE_OF", "I");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/Entity", "getId", "()I");
			mv.visitInsn(IMUL);
			mv.visitFieldInsn(PUTFIELD, owner, "$stride", "I");
			Label l1 = new Label();
			mv.visitLabel(l1);
		}
		mv.visitInsn(RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "Lcom/artemis/component/TransPackedFloatReference;", null, l0, l2, 0);
		mv.visitLocalVariable("e", "Lcom/artemis/Entity;", null, l0, l2, 1);
		mv.visitEnd();
	}
	
	private void injectReset() {
		String owner = meta.type.getInternalName();
		List<FieldDescriptor> fields = ClassMetadataUtil.instanceFields(meta);
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "reset", "()V", null, null);
		mv.visitCode();
		
		Label l0 = new Label();
		mv.visitLabel(l0);
		for (FieldDescriptor fd : fields) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(TypedOpcodes.tCONST(fd));
			mv.visitFieldInsn(PUTFIELD, owner, fd.name, fd.desc);
		}
		
		mv.visitInsn(RETURN);
		
		Label l3 = new Label();
		mv.visitLabel(l3);
		
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l3, 0);
		mv.visitEnd();
	}
}
