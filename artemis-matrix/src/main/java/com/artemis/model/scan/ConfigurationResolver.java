package com.artemis.model.scan;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.reflections.Reflections;

import com.artemis.Component;
import com.artemis.EntitySystem;
import com.artemis.Manager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.systems.VoidEntitySystem;

public final class ConfigurationResolver {
	final Set<Type> managers;
	final Set<Type> systems;
	final Set<Type> components;
	
	public ConfigurationResolver(String basePackage) {
		systems = findSystems(basePackage);
		managers = findManagers(basePackage);
		components = findComponents(basePackage);
	}
	
	public ArtemisTypeData scan(ClassReader source) {
		ArtemisTypeData info = new ArtemisTypeData();
		source.accept(new ArtemisTypeScanner(info, this), 0);
		return info;
	}
	
	private Set<Type> findManagers(String basePackage) {
		Reflections reflections = new Reflections(basePackage);
		Set<Class<Manager>> managers = new HashSet<Class<Manager>>();
		reursivelyGetSubTypes(reflections, Manager.class, managers);
		return asTypes(managers);
	}
	
	private Set<Type> findSystems(String basePackage) {
		// annoying that Reflections can't recurse subclasses by itself, unless
		// I'm missing something...
		Set<Class<? extends EntitySystem>> systemTypes = new HashSet<Class<? extends EntitySystem>>();
		systemTypes.add(EntitySystem.class);
		systemTypes.add(EntityProcessingSystem.class);
		systemTypes.add(IntervalEntitySystem.class);
		systemTypes.add(IntervalEntityProcessingSystem.class);
		systemTypes.add(VoidEntitySystem.class);
		
		Reflections reflections = new Reflections(basePackage);
		Set<Class<EntitySystem>> systems = new HashSet<Class<EntitySystem>>();
		for (Class<? extends EntitySystem> es : systemTypes)
			reursivelyGetSubTypes(reflections, es, systems);
		
		return asTypes(systems);
	}
	
	private Set<Type> findComponents(String basePackage) {
		Reflections reflections = new Reflections(basePackage);
		Set<Class<Component>> components = new HashSet<Class<Component>>();
		reursivelyGetSubTypes(reflections, Component.class, components);
		return asTypes(components);
	}
	
	private static <T> Set<Type> asTypes(Set<Class<T>> systems) {
		Set<Type> types = new HashSet<Type>();
		for (Class<T> clazz : systems) {
			types.add(Type.getType(clazz));
		}
		return types;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> void reursivelyGetSubTypes(Reflections reflections, Class<? extends T> klazz, Set<Class<T>> dest) {
		for (Class<? extends T> subclass : reflections.getSubTypesOf(klazz)) {
			dest.add((Class<T>)subclass);
			reursivelyGetSubTypes(reflections, (Class<T>)subclass, dest);
		}
	}
}
