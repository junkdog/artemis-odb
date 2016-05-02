package com.artemis.weaver.transplant;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ClassTransplantAdapter extends ClassVisitor implements Opcodes {
	protected final ClassReader source;
	private final ClassTransplantVisitor transplanter;

	public ClassTransplantAdapter(ClassReader source,
	                              ClassVisitor target,
	                              ClassMetadata meta,
	                              String name) {

		super(ASM5, target);
		this.source = source;
		transplanter = new ClassTransplantVisitor(source, this, meta, name);
	}

	public ClassTransplantAdapter(Class<?> source,
	                              ClassVisitor target,
	                              ClassMetadata meta,
	                              String name) {

		this(Weaver.toClassReader(source), target, meta, name);
	}

	@Override
	public void visitEnd() {
		source.accept(transplanter, 0);
		super.visitEnd();
	}
}
