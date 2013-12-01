package com.artemis.transformer;

import org.objectweb.asm.tree.MethodNode;

public abstract class MethodTransformer {
	protected MethodTransformer mt;

	public MethodTransformer(MethodTransformer mt) {
		this.mt = mt;
	}

	public boolean transform(MethodNode mn) {
		if (mt != null) {
			return mt.transform(mn);
		}
		return false;
	}
}
