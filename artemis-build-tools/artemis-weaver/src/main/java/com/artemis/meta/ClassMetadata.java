package com.artemis.meta;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

public final class ClassMetadata {
	public WeaverType annotation = WeaverType.NONE;
	
	public boolean isPreviouslyProcessed;
	
	// ie superclass is com/artemis/systems/EntityProcessingSystem or
	// com/artemis/systems/IteratingSystem
	public OptimizationType sysetemOptimizable = OptimizationType.NOT_OPTIMIZABLE;
		
	// methods
	public boolean foundReset;

	// profiler annotation
	public boolean profilingEnabled;
	public Type profilerClass;
	public boolean foundBegin;
	public boolean foundEnd;
	public boolean foundInitialize;
	
	public Type type;
	public String superClass;

	public List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>(); 
	private List<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();

	// pooled components
	public boolean forcePooledWeaving;

	public boolean foundEntityLinks() {
		for (FieldDescriptor f : fields) {
			if (f.entityLinkMutator != null)
				return true;
		}

		return false;
	}

	public MethodDescriptor method(String name, String desc) {
		for (MethodDescriptor md : methods) {
			if (md.name.equals(name) && md.desc.equals(desc))
				return md;
		}

		return null;
	}

	public enum WeaverType { NONE, POOLED }

	public enum OptimizationType { NOT_OPTIMIZABLE, SAFE, FULL }

	public FieldDescriptor field(String name) {
		for (FieldDescriptor f : fields) {
			if (name.equals(f.name))
				return f;
		}

		FieldDescriptor fd = new FieldDescriptor(name);
		fields.add(fd);

		return fd;
	}

	public List<FieldDescriptor> fields() {
		return fields;
	}

	public static class GlobalConfiguration {
		public static boolean enabledPooledWeaving;
		public static boolean optimizeEntitySystems;
		public static boolean generateLinkMutators;
	}
}
