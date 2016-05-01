package com.artemis.meta;

import org.objectweb.asm.tree.AbstractInsnNode;

public class FieldDescriptor {
	public int access;
	public String name;
	public String desc;
	public String signature;
	public Object value;
	public AbstractInsnNode reset;
	public Class<?> entityLinkMutator;

	public FieldDescriptor(String name) {
		this.name = name;
	}

	public void set(int access, String desc, String signature, Object value) {
		this.access = access;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}

	public boolean isResettable() {
		// desc == null; means we're actually dealing with a reference to a field
		// in a parent class; tricky... but it mostly affects packed components, so
		// we'll leave it here for now
		return desc != null && (desc.length() == 1 || "Ljava/lang/String;".equals(desc));
	}

	@Override
	public String toString() {
		return "FieldDescriptor{" +
			"name='" + name + '\'' +
			", desc='" + desc + '\'' +
			", signature='" + signature + '\'' +
			'}';
	}
}
