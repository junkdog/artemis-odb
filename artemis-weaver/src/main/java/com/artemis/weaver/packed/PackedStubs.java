package com.artemis.weaver.packed;

import static com.artemis.meta.ClassMetadataUtil.instanceFieldTypes;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.util.List;
import java.util.Set;

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
		if (!meta.foundStaticInitializer)
			injectStaticInitializer();
		
		if (!meta.foundEntityFor)
			injectForEntity();
		
		// inject sizeof
		Set<String> types = instanceFieldTypes(meta);
		if (types.size() > 1) {
			System.err.println("Expected one type, found: " + types);
		}
		
		List<FieldDescriptor> dataFields = instanceFields(meta);
		cw.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "$_SIZE_OF", "I", null,
			Integer.valueOf(dataFields.size())).visitEnd();;
		cw.visitField(ACC_PRIVATE, "$offset", "I", null, Integer.valueOf(0)).visitEnd();
		
		injectReset();
		// inject array & inject $grow()
		if (dataFields.size() > 0) {
			
			cw.visitField(ACC_PRIVATE + ACC_STATIC, "$data", "[" + dataFields.get(0).desc, null, null).visitEnd();
			String dataDesc = instanceFields(meta).get(0).desc;
			injectGrow(meta.type.getInternalName(), arrayTypeDesc(dataDesc), arrayTypeInst(dataDesc));
			
			FieldToArrayClassTransformer transformer = new FieldToArrayClassTransformer(meta);
			ClassNode cn = transformer.transform(cr);
			
			cn.accept(cw);
		} else {
			cr.accept(cw, 0);
		}
		return cw.toByteArray();
	}

	private void injectGrow(String owner, String arrayTypeDesc, int arrayTypeInst) {
		MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "$grow", "()V", null, null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, owner, "$data", arrayTypeDesc);
		mv.visitVarInsn(ASTORE, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitInsn(ICONST_2);
		mv.visitInsn(IMUL);
		mv.visitIntInsn(NEWARRAY, arrayTypeInst);
		mv.visitFieldInsn(PUTSTATIC, owner, "$data", arrayTypeDesc);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_0);
		mv.visitFieldInsn(GETSTATIC, owner, "$data", arrayTypeDesc);
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARRAYLENGTH);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}

	
	private void injectStaticInitializer() {
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}
	
	private void injectForEntity() {
		String owner = meta.type.getInternalName();
		String dataDesc = instanceFields(meta).get(0).desc;
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "forEntity", "(Lcom/artemis/Entity;)Lcom/artemis/PackedComponent;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		injectIntValue(mv, instanceFields(meta).size());
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/Entity", "getId", "()I");
		mv.visitInsn(IMUL);
		mv.visitFieldInsn(PUTFIELD, owner, "$offset", "I");
		mv.visitFieldInsn(GETSTATIC, owner, "$data", arrayTypeDesc(dataDesc));
		mv.visitInsn(ARRAYLENGTH);
		mv.visitInsn(ICONST_1);
		mv.visitInsn(ISUB);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, owner, "$offset", "I");
		Label l2 = new Label();
		mv.visitJumpInsn(IF_ICMPGT, l2);
		mv.visitMethodInsn(INVOKESTATIC, owner, "$grow", "()V");
		mv.visitLabel(l2);
		mv.visitFrame(F_SAME, 0, null, 0, null);
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
		TypedOpcodes opcodes = new TypedOpcodes(meta);
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "reset", "()V", null, null);
		mv.visitCode();
		
		Label l0 = new Label();
		mv.visitLabel(l0);
		
		for (int i = 0; fields.size() > i; i++) {
			mv.visitFieldInsn(GETSTATIC, owner, "$data", arrayTypeDesc(fields.get(0).desc));
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, owner, "$offset", "I");
			mv.visitInsn(ICONST_0);
			mv.visitInsn(IADD);
			mv.visitInsn(opcodes.tCONST());
			mv.visitInsn(opcodes.tASTORE());
		}
		
		mv.visitInsn(RETURN);
		
		Label l3 = new Label();
		mv.visitLabel(l3);
		
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l3, 0);
		mv.visitEnd();
	}
	
	private static void injectIntValue(MethodVisitor methodVisitor, int value) {
		if (value > (ICONST_5 - ICONST_0))
			methodVisitor.visitIntInsn(BIPUSH, value);
		else
			methodVisitor.visitInsn(ICONST_0 + value);
	}
	

	private static String arrayTypeDesc(String dataDesc) {
		return "[" + dataDesc;
	}
	
	private static int arrayTypeInst(String dataDesc) {
		assert (dataDesc.length() == 1);
		
		switch (dataDesc.charAt(0)) {
			case 'I':
				return T_INT;
			case 'J':
				return T_LONG;
			case 'S':
				return T_SHORT;
			case 'B':
				return T_BYTE;
			case 'C':
				return T_CHAR;
			case 'F':
				return T_FLOAT;
			case 'D':
				return T_DOUBLE;
			case 'Z':
				return T_BOOLEAN;
			case 'A':
			default:
				throw new RuntimeException("Unknown array type for " + dataDesc);
		}
	}
}
