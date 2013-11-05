package com.artemis.weaver;

import static com.artemis.meta.ClassMetadataUtil.instanceFieldTypes;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.FieldDescriptor;
import com.artemis.weaver.packed.FieldToArrayClassTransformer;
import com.artemis.weaver.packed.PackedComponentWeaver;
import com.artemis.weaver.pooled.PooledComponentWeaver;

public class ComponentTypeTransmuter extends CallableTransmuter implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;
	
	public ComponentTypeTransmuter(String file, ClassReader cr, ClassMetadata meta) {
		super(file);
		this.cr = cr;
		this.meta = meta;
	}
	
	@Override
	protected void process(String file) throws FileNotFoundException, IOException {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		if (WeaverType.PACKED == meta.annotation)
			injectPackedComponentStubs();
		if (!meta.foundReset)
			injectMethodStub("reset", "()V");
		
		compileClass(meta, file);
	}

	private void injectPackedComponentStubs() {
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
		
		// inject array & inject $grow()
		if (dataFields.size() > 0) {
			cw.visitField(ACC_PRIVATE + ACC_STATIC, "$data", "[" + dataFields.get(0).desc, null, null).visitEnd();
			String dataDesc = instanceFields(meta).get(0).desc;
			injectGrow(meta.type.getInternalName(), arrayTypeDesc(dataDesc), arrayTypeInst(dataDesc));
			
			FieldToArrayClassTransformer transformer = new FieldToArrayClassTransformer(null, meta);
			ClassNode cn = new ClassNode(ASM4);
			cr.accept(cn, 0);
			transformer.transform(cn);
			
			cn.accept(cw);
		} else {
			cr.accept(cw, 0);
		}
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
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

	private static String arrayTypeDesc(String dataDesc) {
		return "[" + dataDesc;
	}

	private static int arrayTypeInst(String dataDesc) {
		assert (dataDesc.length() == 1);
		
		switch (dataDesc.charAt(0)) {
			case 'I':
				return T_INT;
			case 'L':
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

	private void compileClass(ClassMetadata meta, String file) {
		ClassVisitor cv = cw;
		switch (meta.annotation) {
			case PACKED:
				cv = new PackedComponentWeaver(new CommonClassWeaver(cv, meta), meta);
				break;
			case POOLED:
				cv = new PooledComponentWeaver(new CommonClassWeaver(cv, meta), meta);
				break;
			case NONE:
				break;
			default:
				throw new IllegalArgumentException("Missing case: " + meta.annotation);
		}

		try {
			cr.accept(cv, ClassReader.EXPAND_FRAMES);
			
			if (file != null)
				ClassUtil.writeClass(cw, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ClassWriter getClassWriter() {
		return cw;
	}
	
	private void injectStaticInitializer() {
		MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}
	
	private static int insn(String dataDesc) {
		assert (dataDesc.length() == 1);
		switch (dataDesc.charAt(0)) {
			case 'I':
				return T_INT;
			case 'L':
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
				throw new RuntimeException("Unknown type for " + dataDesc);
		}
	}
	
	private void injectForEntity() {
		String owner = meta.type.getInternalName();
		String dataDesc = instanceFields(meta).get(0).desc;
		
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "forEntity", "(Lcom/artemis/Entity;)Lcom/artemis/PackedComponent;", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(18, l0);
		mv.visitVarInsn(ALOAD, 0);
		injectIntValue(mv, instanceFields(meta).size());
//		mv.visitInsn(ICONST_2);
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
		mv.visitLineNumber(20, l2);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		Label l3 = new Label();
		mv.visitLabel(l3);
//		mv.visitLocalVariable("this", "Lcom/artemis/component/TransPackedFloatReferencel;", null, l0, l3, 0);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l3, 0);
		mv.visitLocalVariable("e", "Lcom/artemis/Entity;", null, l0, l3, 1);
//		mv.visitMaxs(3, 2);
		mv.visitEnd();
		
//		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, "reset", "()V", null, null);
//		mv.visitCode();
//		Label lBegin = new Label();
//		mv.visitLabel(lBegin);
//		mv.visitFieldInsn(GETSTATIC, owner, "$data", arrayTypeDesc(dataDesc));
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitFieldInsn(GETFIELD, owner, "$offset", "I");
//		mv.visitInsn(ICONST_0);
//		mv.visitInsn(IADD);
//		mv.visitInsn(FCONST_0);
//		mv.visitInsn(FASTORE);
//		mv.visitFieldInsn(GETSTATIC, owner, "$data", arrayTypeDesc(dataDesc));
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitFieldInsn(GETFIELD, owner, "$offset", "I");
//		mv.visitInsn(ICONST_1);
//		mv.visitInsn(IADD);
//		mv.visitInsn(FCONST_0);
//		mv.visitInsn(FASTORE);
//		mv.visitInsn(RETURN);
//		Label lEnd = new Label();
//		mv.visitLabel(lEnd);
//		mv.visitLocalVariable("this", owner, null, lBegin, lEnd, 0);
//		mv.visitMaxs(3, 1);
//		mv.visitEnd();
		
		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	}
	
	private static void injectIntValue(MethodVisitor methodVisitor, int value) {
		if (value > (ICONST_5 - ICONST_0))
			methodVisitor.visitIntInsn(BIPUSH, value);
		else
			methodVisitor.visitInsn(ICONST_0 + value);
	}
	
	private void injectMethodStub(String name, String description) {
		MethodVisitor method = cw.visitMethod(ACC_PUBLIC, name, description, null, null);
		method.visitCode();
		method.visitLabel(new Label());
		method.visitInsn(RETURN);
		method.visitEnd();

		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	}
}
