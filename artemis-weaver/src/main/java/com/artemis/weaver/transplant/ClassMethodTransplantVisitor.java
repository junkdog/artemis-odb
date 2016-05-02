package com.artemis.weaver.transplant;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.MethodDescriptor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

class ClassMethodTransplantVisitor extends ClassVisitor {
	private final ClassVisitor target;
	private final ClassMetadata meta;
	private final ClassReader source;
	private List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>();

	public ClassMethodTransplantVisitor(ClassReader source, ClassVisitor target, ClassMetadata meta) {
		super(Opcodes.ASM5);
		this.target = target;
		this.meta = meta;
		this.source = source;
	}

	public void addMethod(MethodDescriptor method) {
		assert (method != null);
		methods.add(method);
	}

	@Override
	public MethodVisitor visitMethod(int access,
	                                 String name,
	                                 String desc,
	                                 String signature,
	                                 String[] exceptions) {

		if (!contains(name, desc))
			return null;

		MethodVisitor mv = target.visitMethod(access, name, desc, signature, exceptions);
		return new MethodBodyTransplanter(source.getClassName(), meta, mv);
	}

	private boolean contains(String name, String desc) {
		for (MethodDescriptor md : methods) {
			if (md.name.equals(name) && md.desc.equals(desc))
				return true;
		}

		return false;
	}
}
