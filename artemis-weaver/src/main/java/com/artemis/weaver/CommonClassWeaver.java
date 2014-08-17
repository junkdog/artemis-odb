package com.artemis.weaver;


import static com.artemis.meta.ClassMetadataUtil.instanceFields;
import static com.artemis.meta.ClassMetadataUtil.superName;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;

class CommonClassWeaver extends ClassVisitor implements Opcodes {

	private ClassMetadata meta;
	
	public CommonClassWeaver(ClassVisitor cv, ClassMetadata meta) {
		super(ASM4, cv);
		this.meta = meta;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (meta.annotation == WeaverType.PACKED && instanceFields(meta).size() > 0) {
			List<String> interfaceList = Arrays.asList("com/artemis/PackedComponent$DisposedWithWorld");
			if (interfaces != null) {
				interfaceList.addAll(Arrays.asList(interfaces));
			}
			interfaces = interfaceList.toArray(new String[0]);
		}
		cv.visit(version, access, name, signature, superName(meta), interfaces);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return (Weaver.PACKED_ANNOTATION.equals(desc) || Weaver.POOLED_ANNOTATION.equals(desc))
			? null
			: super.visitAnnotation(desc, visible);
	}
}
