package com.artemis.model.scan;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

import org.objectweb.asm.Type;

/**
 * Annotation blob bloat.
 */
@ToString
public class ArtemisConfigurationData {
	public static enum AnnotationType {SYSTEM, MANAGER, POJO}
	
	// artemis configuration annotation
	public final List<Type> requires = new ArrayList<Type>();
	public final List<Type> requiresOne = new ArrayList<Type>();
	public final List<Type> optional = new ArrayList<Type>();
	public final List<Type> exclude = new ArrayList<Type>();
	public final List<Type> systems = new ArrayList<Type>();
	public final List<Type> managers = new ArrayList<Type>();
	public AnnotationType annotationType;
	
	public Type current;
	
	ArtemisConfigurationData() {}
	
	public boolean is(AnnotationType type) {
		return annotationType == type;
	}
}
