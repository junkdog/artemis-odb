package com.artemis.weaver;

import static com.artemis.meta.ClassMetadataUtil.instanceFieldTypes;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.io.FileNotFoundException;
import java.io.IOException;
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
		if (!meta.foundEntityFor)
			injectForEntity();
		
		// inject sizeof
		Set<String> types = instanceFieldTypes(meta);
		if (types.size() > 1) {
			System.err.println("Expected one type, found: " + types);
		}
		
		cw.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "$_SIZE_OF", "I", null,
			Integer.valueOf(instanceFields(meta).size())).visitEnd();;
		
		// inject array
		
		// inject grow() 
		
		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
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
