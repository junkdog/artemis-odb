package com.artemis.weaver.packed;

import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.artemis.ClassUtil;
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
//		if (!meta.foundStaticInitializer)
//			injectStaticInitializer();
		
		if (!meta.foundEntityFor)
			injectForEntity();
		
		List<FieldDescriptor> dataFields = instanceFields(meta);
		cw.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "$_SIZE_OF", "I", null,
			Integer.valueOf(ClassMetadataUtil.sizeOf(meta))).visitEnd();;
		cw.visitField(ACC_PRIVATE, "$stride", "I", null, Integer.valueOf(0)).visitEnd();
		
		// Reason for recreating teh ClassReader/Writer combo:
		// To make life simpler, the reset method acts on the original fields, 
		// delegating the ByteBuffer weaving to the FieldToArrayClassTransformer.
		injectReset();
		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		
		if (dataFields.size() > 0) {
			cw.visitField(ACC_PRIVATE, "$data", "Ljava/nio/ByteBuffer;", null, null).visitEnd();
			injectGrow(meta.type.getInternalName());
			
			FieldToStructTransformer transformer = new FieldToStructTransformer(meta);
			ClassNode cn = transformer.transform(cr);
			
			cn.accept(cw);
		} else {
			cr.accept(cw, 0);
		}
		return cw.toByteArray();
	}

	private void injectGrow(String owner) {
		
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "$grow", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "capacity", "()I");
		mv.visitInsn(ICONST_2);
		mv.visitInsn(IMUL);
		mv.visitMethodInsn(INVOKESTATIC, "java/nio/ByteBuffer", "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
		mv.visitVarInsn(ASTORE, 1);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 2);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "capacity", "()I");
		mv.visitVarInsn(ISTORE, 3);
		Label l3 = new Label();
		mv.visitLabel(l3);
		Label l4 = new Label();
		mv.visitJumpInsn(GOTO, l4);
		Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitFrame(Opcodes.F_APPEND,3, new Object[] {"java/nio/ByteBuffer", Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ILOAD, 2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		mv.visitVarInsn(ILOAD, 2);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "get", "(I)B");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "put", "(IB)Ljava/nio/ByteBuffer;");
		mv.visitInsn(POP);
		Label l6 = new Label();
		mv.visitLabel(l6);
		mv.visitIincInsn(2, 1);
		mv.visitLabel(l4);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ILOAD, 3);
		mv.visitVarInsn(ILOAD, 2);
		mv.visitJumpInsn(IF_ICMPGT, l5);
		Label l7 = new Label();
		mv.visitLabel(l7);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
		Label l8 = new Label();
		mv.visitLabel(l8);
		mv.visitInsn(RETURN);
//		Label l9 = new Label();
//		mv.visitLabel(l9);
//		mv.visitLocalVariable("this", owner, null, l0, l9, 0);
//		mv.visitLocalVariable("newBuffer", "Ljava/nio/ByteBuffer;", null, l1, l9, 1);
//		mv.visitLocalVariable("i", "I", null, l2, l7, 2);
//		mv.visitLocalVariable("s", "I", null, l3, l7, 3);
//		mv.visitMaxs(4, 4);
//		mv.visitEnd();

	}

	
	private void injectStaticInitializer() {
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}
	
	private void injectForEntity() {
		String owner = meta.type.getInternalName();
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "forEntity", "(Lcom/artemis/Entity;)Lcom/artemis/PackedComponent;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		if (meta.fields.size() > 0) {
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETSTATIC, owner, "$_SIZE_OF", "I");
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/Entity", "getId", "()I");
			mv.visitInsn(IMUL);
			mv.visitFieldInsn(PUTFIELD, owner, "$stride", "I");
			
			Label l1 = new Label();
			mv.visitLabel(l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, owner, "$data", "Ljava/nio/ByteBuffer;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "capacity", "()I");
			mv.visitIntInsn(BIPUSH, 8);
			mv.visitInsn(ISUB);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, owner, "$stride", "I");
			Label l2 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l2);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, owner, "$grow", "()V");
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		}
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l3, 0);
		mv.visitLocalVariable("e", "Lcom/artemis/Entity;", null, l0, l3, 1);
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
