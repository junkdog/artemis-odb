package com.artemis.meta;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class MethodDescriptor {
	private final int access;
	private final String name;
	private final String desc;
	private final String signature;
	private final String[] exceptions;
}
