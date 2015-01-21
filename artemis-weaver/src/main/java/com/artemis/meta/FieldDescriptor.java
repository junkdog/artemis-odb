package com.artemis.meta;

public class FieldDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final Object value;
	public int offset; // byte offset, only used by @PackedComponent

	public FieldDescriptor(int access, String name, String desc, String signature, Object value) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}
}
