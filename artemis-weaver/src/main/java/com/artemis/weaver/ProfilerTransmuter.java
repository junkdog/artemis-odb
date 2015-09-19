package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.profile.ProfileVisitor;

/**
 * Rewrites access to packed components so that related classes can
 * access packed components with direct field access, syntactically.
 */
public class ProfilerTransmuter extends CallableTransmuter<Void> implements Opcodes {
	private ClassReader cr;
	private ClassWriter cw;
	private final ClassMetadata meta;
	
	public ProfilerTransmuter(String file, ClassMetadata meta, ClassReader cr) {
		super(file);
		this.meta = meta;
		this.cr = cr;
	}
	
	@Override
	protected Void process(String file) throws IOException {
		injectProfilerStubs(meta);
		
		ClassVisitor cv = new ProfileVisitor(cw, meta);
		cv = new ProfileAnnotationRemoverWeaver(cv);
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
		
		ClassUtil.writeClass(cw, file);
		return null;
	}
	
	private void injectProfilerStubs(ClassMetadata meta) {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		FieldVisitor fv = cw.visitField(ACC_PRIVATE|ACC_FINAL, "$profiler", meta.profilerClass.getDescriptor(), null, null);
		fv.visitEnd();

		if (!meta.foundInitialize)
			ClassUtil.injectMethodStub(cw, "initialize");
		if (!meta.foundBegin)
			ClassUtil.injectMethodStub(cw, "begin");
		if (!meta.foundEnd)
			ClassUtil.injectMethodStub(cw, "end");

		cr.accept(cw, 0);
		cr = new ClassReader(cw.toByteArray());
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	}
}
