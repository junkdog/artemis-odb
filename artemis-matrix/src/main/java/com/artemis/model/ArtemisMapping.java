package com.artemis.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

import com.artemis.model.scan.ArtemisTypeData;

public final class ArtemisMapping {
	public final Type system;
	public final boolean isSystem;
	public final boolean isManager;
	public final ComponentReference[] componentIndices;
	public final String name;
	public final String[] refSystems;
	public final String[] refManagers;
	
	public final boolean isPackage; // referenced by chtml
	
	public ArtemisMapping(String packageName) {
		name = packageName;
		system = null;
		refSystems = null;
		refManagers = null;
		componentIndices = null;
		
		isPackage = true;
		isSystem = false;
		isManager = false;
	}

	private ArtemisMapping(ArtemisTypeData system, ComponentReference[] componentIndices) {
		this.system = system.current;
		this.componentIndices = componentIndices;
		
		name = shortName(this.system);
		
		refManagers = new String[system.managers.size()];
		// FIXME
//		for (int i = 0; system.managers.size() > i; i++)
//			refManagers[i] = shortName(system.managers.get(i));
		
		refSystems = new String[system.systems.size()];
		// FIXME
//		for (int i = 0; system.systems.size() > i; i++)
//			refSystems[i] = shortName(system.systems.get(i));
		
		// FIXME
//		isSystem = system.is(SYSTEM);
//		isManager = system.is(MANAGER);
		isPackage = false;
		isSystem = false;
		isManager = false;
	}
	
	public static ArtemisMapping from(ArtemisTypeData system,
		Map<Type, Integer> componentIndices) {
		ComponentReference[] components = new ComponentReference[componentIndices.size()];
		Arrays.fill(components, ComponentReference.NOT_REFERENCED);
		// FIXME
//		mapComponents(system.requires, ComponentReference.REQUIRED, componentIndices, components);
//		mapComponents(system.requiresOne, ComponentReference.ANY, componentIndices, components);
//		mapComponents(system.optional, ComponentReference.OPTIONAL, componentIndices, components);
//		mapComponents(system.exclude, ComponentReference.EXCLUDED, componentIndices, components);
		
		return new ArtemisMapping(system, components);
	}
	
	public String getName() {
		return shortName(system);
	}
	
	private static String shortName(Type type) {
		String name = type.getClassName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	private static void mapComponents(List<Type> references, ComponentReference referenceType, Map<Type,Integer> componentIndices,
		ComponentReference[] components) {
		for (Type component : references)
			components[componentIndices.get(component)] = referenceType;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		sb.append('"').append(getName()).append('"');
		for (ComponentReference ref : componentIndices) {
			sb.append(", \"").append(ref.symbol).append('"');
		}
		sb.append(" ]");
		
		return sb.toString();
	}
}