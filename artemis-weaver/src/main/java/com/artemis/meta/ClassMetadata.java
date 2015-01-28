package com.artemis.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.objectweb.asm.Type;

public final class ClassMetadata {
	public WeaverType annotation = WeaverType.NONE;
	
	public boolean isPreviouslyProcessed;
	
	//  ie superclass is com/artemis/systems/EntityProcessingSystem
	public OptimizationType sysetemOptimizable = OptimizationType.NOT_OPTIMIZABLE;
		
	// methods
	public boolean foundReset;
	public boolean foundEntityFor;

	// profiler annotation
	public boolean profilingEnabled;
	public Type profilerClass;
	public boolean foundBegin;
	public boolean foundEnd;
	public boolean foundInitialize;
	
	public Type type;
	public String superClass;

	public List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>(); 
	public List<FieldDescriptor> fields = new ArrayList<FieldDescriptor>();

	// pooled components
	public boolean forcePooledWeaving;
	
	// packed components
	public boolean foundStaticInitializer;
	public boolean directFieldAccess;


	public static enum WeaverType { NONE, POOLED, PACKED };
	public static enum OptimizationType { NOT_OPTIMIZABLE, SAFE, FULL };
	
	public Future<?> weaverTask;

	public static class GlobalConfiguration {
		public static boolean ideFriendlyPacking;
		public static boolean enabledPooledWeaving;
		public static boolean enabledPackedWeaving;
		public static boolean optimizeEntitySystems;
	}
}
