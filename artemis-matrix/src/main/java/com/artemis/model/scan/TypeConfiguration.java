package com.artemis.model.scan;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Type;

public class TypeConfiguration {
	public Set<Type> components;
	public Set<Type> managers;
	public Set<Type> systems;

	public TypeConfiguration() {
		components = new HashSet<Type>();
		addType("com.artemis.Component", components);
		addType("com.artemis.PooledComponent", components);
		addType("com.artemis.PackedComponent", components);
		
		managers = new HashSet<Type>();
		addType("com.artemis.Manager", managers);
		
		systems = new HashSet<Type>();
		addType("com.artemis.EntitySystem", systems);
		addType("com.artemis.systems.EntityProcessingSystem", systems);
		addType("com.artemis.systems.IntervalEntitySystem", systems);
		addType("com.artemis.systems.IntervalEntityProcessingSystem", systems);
		addType("com.artemis.systems.VoidEntitySystem", systems);
	}
	
	static Type type(String klazz) {
		klazz = klazz.replace('.', '/');
		return Type.getType("L" + klazz + ";");
	}
	
	private static void addType(String qualifiedName, Set<Type> containerType) {
		containerType.add(type(qualifiedName));
	}
}