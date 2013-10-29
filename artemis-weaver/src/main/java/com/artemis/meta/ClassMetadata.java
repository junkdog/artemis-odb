package com.artemis.meta;

import org.objectweb.asm.Type;

import lombok.ToString;

@ToString
public final class ClassMetadata {
	public WeaverType annotation = WeaverType.NONE;
	
	public boolean isPreviouslyProcessed;
	
	// methods
	public boolean foundReset;
	public boolean foundEntityFor;

	public Type type;

	public static enum WeaverType { NONE, POOLED, PACKED };
}
