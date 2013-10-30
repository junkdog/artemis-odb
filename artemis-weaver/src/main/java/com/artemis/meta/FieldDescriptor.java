package com.artemis.meta;

import lombok.Data;

@Data
public class FieldDescriptor {
	private final int access;
	private final String name;
	private final String desc;
	private final String signature;
	private final Object value;
}
