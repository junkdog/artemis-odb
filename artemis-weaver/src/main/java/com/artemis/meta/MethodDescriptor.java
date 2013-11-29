package com.artemis.meta;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor @ToString
public class MethodDescriptor {
	public final int access;
	public final String name;
	public final String desc;
	public final String signature;
	public final String[] exceptions;
}
