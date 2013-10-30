package com.artemis.meta;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

import org.objectweb.asm.Type;

@ToString
public final class ClassMetadata {
	public WeaverType annotation = WeaverType.NONE;
	
	public boolean isPreviouslyProcessed;
	
	// methods
	public boolean foundReset;
	public boolean foundEntityFor;

	public Type type;
	public String superClass;

	public List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>(); 
	public List<FieldDescriptor> fields = new ArrayList<FieldDescriptor>(); 

	public static enum WeaverType { NONE, POOLED, PACKED };
}
