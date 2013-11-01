package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;

public class ComponentTypeWeaver extends CallableWeaver implements Opcodes{
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;
	
	public ComponentTypeWeaver(String file, ClassReader cr, ClassMetadata meta)
	{
		super(file);
		this.cr = cr;
		this.meta = meta;
	}
	
	@Override
	protected void process(String file) throws FileNotFoundException, IOException {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		if (meta.annotation == WeaverType.PACKED && !meta.foundEntityFor)
			injectMethod("forEntity", "(Lcom/artemis/Entity;)V");
		if (!meta.foundReset)
			injectMethod("reset", "()V");
		
		compileClass(meta, file);
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
	
	private void injectMethod(String name, String description) {
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
