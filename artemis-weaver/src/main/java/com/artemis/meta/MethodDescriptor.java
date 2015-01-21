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
}
