package com.artemis.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {
	public ClassNode transform(ClassReader cr);
}
