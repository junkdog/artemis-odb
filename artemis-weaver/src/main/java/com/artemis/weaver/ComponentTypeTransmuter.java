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
import com.artemis.weaver.packed.PackedComponentWeaver;
import com.artemis.weaver.packed.PackedStubs;
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
		if (meta.annotation == WeaverType.PACKED)
			cr = new PackedStubs(cr, meta).transform();
		if (!meta.foundReset)
			injectMethodStub("reset", "()V");
		
		compileClass(meta, file);
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
				return;
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
	
	private void injectMethodStub(String name, String description) {
		MethodVisitor method = cw.visitMethod(ACC_PUBLIC, name, description, null, null);
		method.visitCode();
		method.visitLabel(new Label());
		method.visitInsn(RETURN);
		method.visitEnd();
	}
}
