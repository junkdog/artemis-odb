package com.artemis.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.objectweb.asm.Type;

import com.artemis.model.scan.ArtemisTypeData;
import com.artemis.model.scan.ConfigurationResolver;
import com.artemis.util.MatrixStringUtil;

public final class ArtemisTypeMapping {
	public final Type artemisType;
	public final boolean isSystem;
	public final boolean isManager;
	public final ComponentReference[] componentIndices;
	public final String name;
	public final String[] refSystems;
	public final String[] refManagers;
	
	public final boolean isPackage; // referenced by chtml
	
	public ArtemisTypeMapping(String packageName) {
		name = packageName;
		artemisType = null;
		refSystems = null;
		refManagers = null;
		componentIndices = null;
		
		isPackage = true;
		isSystem = false;
		isManager = false;
	}

	private ArtemisTypeMapping(ArtemisTypeData typeData, ConfigurationResolver resolver, ComponentReference[] componentIndices) {
		this.artemisType = typeData.current;
		this.componentIndices = componentIndices;
		
		name = MatrixStringUtil.shortName(this.artemisType);
		
		refManagers = new String[typeData.managers.size()];
		int index = 0;
		for (Type manager : typeData.managers) {
			refManagers[index++] = MatrixStringUtil.shortName(manager);
		}
		
		refSystems = new String[typeData.systems.size()];
		
		index = 0;
		for (Type es : typeData.systems) {
			refSystems[index++] = MatrixStringUtil.shortName(es);
		}
		isPackage = false;
		isSystem = resolver.systems.contains(this.artemisType);
		isManager = resolver.managers.contains(this.artemisType);
	}
	
	private static void filterComponentMappings(ArtemisTypeData typeData) {
		typeData.optional.removeAll(typeData.requires);
		typeData.optional.removeAll(typeData.requiresOne);
		typeData.optional.removeAll(typeData.exclude);
	}

	public static ArtemisTypeMapping from(ArtemisTypeData typeData, ConfigurationResolver resolver,
		Map<Type, Integer> componentIndices) {
		
		filterComponentMappings(typeData);
		
		ComponentReference[] components = new ComponentReference[componentIndices.size()];
		Arrays.fill(components, ComponentReference.NOT_REFERENCED);
		
		mapComponents(typeData.requires, ComponentReference.REQUIRED, componentIndices, components);
		mapComponents(typeData.requiresOne, ComponentReference.ANY, componentIndices, components);
		mapComponents(typeData.optional, ComponentReference.OPTIONAL, componentIndices, components);
		mapComponents(typeData.exclude, ComponentReference.EXCLUDED, componentIndices, components);
		
		return new ArtemisTypeMapping(typeData, resolver, components);
	}
	
	public String getName() {
		return MatrixStringUtil.shortName(artemisType);
	}
	
	private static void mapComponents(Collection<Type> references, ComponentReference referenceType, Map<Type,Integer> componentIndices,
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