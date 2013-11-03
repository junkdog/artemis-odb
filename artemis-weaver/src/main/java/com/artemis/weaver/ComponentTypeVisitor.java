package com.artemis.weaver;


import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;
import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.ClassMetadataUtil;

public class ComponentTypeVisitor extends ClassVisitor implements Opcodes{

	private ClassMetadata meta;
	
	public ComponentTypeVisitor(ClassVisitor cv, ClassMetadata meta)
	{
		super(ASM4, cv);
		this.meta = meta;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName(meta.annotation), interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return (Weaver.PACKED_ANNOTATION.equals(desc) || Weaver.POOLED_ANNOTATION.equals(desc))
			? null
			: super.visitAnnotation(desc, visible);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor method = cv.visitMethod(access, name, desc, signature, exceptions);
		if ("<init>".equals(name))
			method = new ConstructorInvocationVisitor(method, meta);
		if ("reset".equals(name) && "()V".equals(desc) && meta.annotation == POOLED) 
			method = new ResetMethodVisitor(method, meta);
		if (meta.annotation == PACKED && instanceFields(meta).size() > 0 && "<clinit>".equals(name))
			method = new StaticInitializerVisitor(method, meta);
		
		return method;
	}
	
	private static String superName(ClassMetadata.WeaverType type) {
		switch (type) {
			case PACKED:
				return "com/artemis/PackedComponent";
			case POOLED:
				return "com/artemis/PooledComponent";
			case NONE:
			default:
				throw new RuntimeException("Missing case : " + type);
		}
	}
}
