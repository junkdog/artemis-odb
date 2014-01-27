package com.artemis.meta;

import lombok.Data;

@Data
public class FieldDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final Object value;

	public FieldDescriptor(int access, String name, String desc, String signature, Object value) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}

	public int getAccess() {
		return access;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getSignature() {
		return signature;
	}

	public Object getValue() {
		return value;
	}
}
