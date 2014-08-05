package com.artemis.weaver.packed;


import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.weaver.ConstructorInvocationVisitor;

public class PackedComponentWeaver extends ClassVisitor implements Opcodes{

	private ClassMetadata meta;
	
	public PackedComponentWeaver(ClassVisitor cv, ClassMetadata meta) {
		super(ASM4, cv);
		this.meta = meta;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor method = null;
		if ("<init>".equals(name) && "()V".equals(desc)) {
			if (PACKED == meta.annotation && instanceFields(meta).size() > 0) {
				// constructor is replaced, so drop the default one.
				return null;
			} else {
				method = cv.visitMethod(access, name, desc, signature, exceptions);
				method = new ConstructorInvocationVisitor(method, meta);
			}
		} else {
			method = cv.visitMethod(access, name, desc, signature, exceptions);
		}
		
		return method;
	}
}
