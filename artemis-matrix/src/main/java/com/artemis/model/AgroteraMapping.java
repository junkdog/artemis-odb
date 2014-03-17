package com.artemis.model;

import static com.artemis.model.scan.ArtemisConfigurationData.AnnotationType.MANAGER;
import static com.artemis.model.scan.ArtemisConfigurationData.AnnotationType.SYSTEM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

import com.artemis.model.scan.ArtemisConfigurationData;

public final class AgroteraMapping {
	public final Type system;
	public final boolean isSystem;
	public final boolean isManager;
	public final ComponentReference[] componentIndices;
	public final String name;
	public final String[] refSystems;
	public final String[] refManagers;
	
	public final boolean isPackage; // referenced by chtml
	
	public AgroteraMapping(String packageName) {
		name = packageName;
		system = null;
		refSystems = null;
		refManagers = null;
		componentIndices = null;
		
		isPackage = true;
		isSystem = false;
		isManager = false;
	}

	private AgroteraMapping(ArtemisConfigurationData system, ComponentReference[] componentIndices) {
		this.system = system.current;
		this.componentIndices = componentIndices;
		
		name = shortName(this.system);
		
		refManagers = new String[system.managers.size()];
		for (int i = 0; system.managers.size() > i; i++)
			refManagers[i] = shortName(system.managers.get(i));
		
		refSystems = new String[system.systems.size()];
		for (int i = 0; system.systems.size() > i; i++)
			refSystems[i] = shortName(system.systems.get(i));
		
		isSystem = system.is(SYSTEM);
		isManager = system.is(MANAGER);
		isPackage = false;
	}
	
	public static AgroteraMapping from(ArtemisConfigurationData system,
		Map<Type, Integer> componentIndices) {
		ComponentReference[] components = new ComponentReference[componentIndices.size()];
		Arrays.fill(components, ComponentReference.NOT_REFERENCED);
		mapComponents(system.requires, ComponentReference.REQUIRED, componentIndices, components);
		mapComponents(system.requiresOne, ComponentReference.ANY, componentIndices, components);
		mapComponents(system.optional, ComponentReference.OPTIONAL, componentIndices, components);
		mapComponents(system.exclude, ComponentReference.EXCLUDED, componentIndices, components);
		
		return new AgroteraMapping(system, components);
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