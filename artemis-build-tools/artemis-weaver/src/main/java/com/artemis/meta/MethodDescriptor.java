package com.artemis.meta;

public class MethodDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final String[] exceptions;

	public MethodDescriptor(int access, String name, String desc, String signature, String[] exceptions) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.exceptions = exceptions;
	}

	public MethodDescriptor(String name, String desc) {
		this(0, name, desc, null, null);
	}

	@Override
	public String toString() {
		return "MethodDescriptor[" +
			", name='" + name + '\'' +
			", desc='" + desc + '\'' +
			", signature='" + signature + '\'' +
			']';
	}
}
