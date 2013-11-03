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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;

public class ComponentTypeWeaver extends CallableWeaver implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;
	
	public ComponentTypeWeaver(String file, ClassReader cr, ClassMetadata meta) {
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
		}
		
		
		cr.accept(cw, 0);
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
		if (meta.annotation != WeaverType.NONE)
			cv = new ComponentTypeVisitor(cv, meta);

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
	
	private void injectForEntity() {
		MethodVisitor method = cw.visitMethod(ACC_PUBLIC, "forEntity", "(Lcom/artemis/Entity;)Lcom/artemis/PackedComponent;", null, null);
		method.visitCode();
		method.visitVarInsn(ALOAD, 0);
		method.visitInsn(ARETURN);
		
		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
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
