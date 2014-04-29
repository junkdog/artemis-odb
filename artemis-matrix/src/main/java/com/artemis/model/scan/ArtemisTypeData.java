package com.artemis.model.scan;

import java.util.HashSet;
import java.util.Set;

import lombok.ToString;

import org.objectweb.asm.Type;

/**
 * Annotation blob bloat.
 */
@ToString
public class ArtemisTypeData {
	public final Set<Type> requires = new HashSet<Type>();
	public final Set<Type> requiresOne = new HashSet<Type>();
	public final Set<Type> optional = new HashSet<Type>();
	public final Set<Type> exclude = new HashSet<Type>();
	public final Set<Type> systems = new HashSet<Type>();
	public final Set<Type> managers = new HashSet<Type>();
	
	public Type current;
	
	ArtemisTypeData() {}
}
