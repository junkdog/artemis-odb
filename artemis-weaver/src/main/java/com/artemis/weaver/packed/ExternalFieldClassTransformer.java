package com.artemis.weaver.packed;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.transformer.ClassTransformer;

public class ExternalFieldClassTransformer extends ClassTransformer {

	private final List<ClassMetadata> packedComponents;
	private boolean needsWriteToDisk;

	public ExternalFieldClassTransformer(ClassVisitor ct, List<ClassMetadata> packedComponents) {
		super(null);
		this.packedComponents = packedComponents;
	}

	@Override @SuppressWarnings("unchecked")
	public void transform(ClassNode cn) {
	
		ExternalFieldMethodTransformer methodTransformer = new ExternalFieldMethodTransformer(null, cn.name, packedComponents);
		
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			needsWriteToDisk |= methodTransformer.transform(method);
		}
		
		try {
			super.transform(cn);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public boolean isNeedsWriteToDisk() {
		return needsWriteToDisk;
	}
}
