package com.artemis.transformer;

import org.objectweb.asm.tree.MethodNode;

public abstract class MethodTransformer {
	protected MethodTransformer ct;

	public MethodTransformer(MethodTransformer ct) {
		this.ct = ct;
	}

	public void transform(MethodNode mn) {
		if (ct != null) {
			ct.transform(mn);
		}
	}
}
