package com.artemis.weaver.packed;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.transformer.ClassTransformer;

public class ExternalFieldClassTransformer implements ClassTransformer, Opcodes {

	private final List<ClassMetadata> packedComponents;
	private boolean needsWriteToDisk;

	public ExternalFieldClassTransformer(ClassVisitor ct, List<ClassMetadata> packedComponents) {
		this.packedComponents = packedComponents;
	}

	@Override @SuppressWarnings("unchecked")
	public ClassNode transform(ClassReader cr) {
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn,  ClassReader.EXPAND_FRAMES);
		
		ExternalFieldMethodTransformer methodTransformer = new ExternalFieldMethodTransformer(null, cn.name, packedComponents);
		
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			needsWriteToDisk |= methodTransformer.transform(method);
		}
		
		return cn;
	}

	public boolean isComponentAccessChanged() {
		return needsWriteToDisk;
	}
}
