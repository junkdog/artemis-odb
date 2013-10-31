package com.artemis.meta;

import lombok.Data;

@Data
public class FieldDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final Object value;
}
