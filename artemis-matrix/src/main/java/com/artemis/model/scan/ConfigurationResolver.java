package com.artemis.model.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.util.ClassFinder;

public final class ConfigurationResolver {
	public Set<Type> managers;
	public Set<Type> systems;
	public Set<Type> components;
	public Set<Type> factories;
	private TypeConfiguration typeConfiguration;
	private final Map<Type,Set<Type>> parentChildrenMap;
	
	public ConfigurationResolver(File rootFolder) {
		if (!rootFolder.isDirectory())
			throw new RuntimeException("Expected folder - " + rootFolder);
		
		managers = new HashSet<Type>();
		systems = new HashSet<Type>();
		components = new HashSet<Type>();
		factories = new HashSet<Type>();
		
		typeConfiguration = new TypeConfiguration();
		systems.addAll(typeConfiguration.systems);
		managers.addAll(typeConfiguration.managers);
		components.addAll(typeConfiguration.components);
		factories.addAll(typeConfiguration.factories);
		
		parentChildrenMap = new HashMap<Type,Set<Type>>();
		
		List<File> classes = ClassFinder.find(rootFolder);
		for (File f : classes) {
			findExtendedArtemisTypes(f); // for resolving children of children
		}
		
		resolveExtendedTypes(typeConfiguration, parentChildrenMap);
		
		for (File f : classes) {
			findArtemisTypes(f);
		}
	}

	private static void resolveExtendedTypes(TypeConfiguration main, Map<Type,Set<Type>> found) {
		main.systems = recursiveResolution(main.systems, found);
		main.managers = recursiveResolution(main.managers, found);
		main.components = recursiveResolution(main.components, found);
		main.factories = recursiveResolution(main.factories, found);
	}

	private static Set<Type> recursiveResolution(Set<Type> types, Map<Type, Set<Type>> found) {
		Set<Type> destination = new HashSet<Type>();
		for (Type t : types) {
			recursiveResolution(t, found, destination);
		}
		
		return destination;
	}

	private static void recursiveResolution(Type t, Map<Type, Set<Type>> found, Set<Type> destination) {
		if (found.containsKey(t)) {
			destination.add(t);
			for (Type foundType : found.get(t)) {
				recursiveResolution(foundType, found, destination);
			}
		}
	}

	public ArtemisTypeData scan(ClassReader source) {
		ArtemisTypeData info = new ArtemisTypeData();
		
		ArtemisScanner typeScanner = new ArtemisScanner(info, this);
		source.accept(typeScanner, 0);
		return info;
	}
	
	public void clearNonDefaultTypes() {
		TypeConfiguration tc = new TypeConfiguration();
		systems.removeAll(tc.systems);
		managers.removeAll(tc.managers);
		components.removeAll(tc.components);
		factories.removeAll(tc.factories);
	}
	
	// TODO: merge with findArtemisType
	private void findExtendedArtemisTypes(FileInputStream stream) {
		ClassReader cr;
		try {
			cr = new ClassReader(stream);
			ParentChainFinder artemisTypeFinder = new ParentChainFinder(parentChildrenMap);
			cr.accept(artemisTypeFinder, 0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void findExtendedArtemisTypes(File file) {
		try {
			findExtendedArtemisTypes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
		}
	}

	private void findArtemisTypes(FileInputStream stream) {
		ClassReader cr;
		try {
			cr = new ClassReader(stream);
			ArtemisTypeFinder artemisTypeFinder = new ArtemisTypeFinder(this, typeConfiguration);
			cr.accept(artemisTypeFinder, 0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void findArtemisTypes(File file) {
		try {
			findArtemisTypes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
		}
	}
}
