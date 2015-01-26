package com.artemis.model.scan;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Type;

class TypeConfiguration {
	protected Set<Type> components;
	protected Set<Type> managers;
	protected Set<Type> systems;
	protected Set<Type> factories;

	public TypeConfiguration() {
		components = new HashSet<Type>();
		addType("com.artemis.Component", components);
		addType("com.artemis.PooledComponent", components);
		addType("com.artemis.PackedComponent", components);
		
		managers = new HashSet<Type>();
		addType("com.artemis.Manager", managers);
		addType("com.artemis.managers.TagManager", managers);
		addType("com.artemis.managers.GroupManager", managers);
		addType("com.artemis.managers.PlayerManager", managers);
		addType("com.artemis.managers.TeamManager", managers);
		addType("com.artemis.managers.UuidEntityManager", managers);
		
		systems = new HashSet<Type>();
		addType("com.artemis.EntitySystem", systems);
		addType("com.artemis.systems.EntityProcessingSystem", systems);
		addType("com.artemis.systems.IntervalEntitySystem", systems);
		addType("com.artemis.systems.IntervalEntityProcessingSystem", systems);
		addType("com.artemis.systems.VoidEntitySystem", systems);

		factories = new HashSet<Type>();
		addType("com.artemis.EntityFactory", factories);
	}
	
	static Type type(String klazz) {
		klazz = klazz.replace('.', '/');
		return Type.getType("L" + klazz + ";");
	}
	
	static void addType(String qualifiedName, Set<Type> containerType) {
		containerType.add(type(qualifiedName));
	}
}