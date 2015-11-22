package com.artemis.meta;

public class FieldDescriptor {
	public int access;
	public String name;
	public String desc;
	public String signature;
	public Object value;
	public int offset; // byte offset, only used by @PackedComponent

	public FieldDescriptor(int access, String name, String desc, String signature, Object value) {
		this.name = name;

		set(access, desc, signature, value);
	}

	public void set(int access, String desc, String signature, Object value) {
		this.access = access;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}

	public FieldDescriptor(String name) {
		this.name = name;
	}
}
