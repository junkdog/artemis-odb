package com.artemis.model;

import static com.artemis.util.MatrixStringUtil.findLongestClassName;
import static com.artemis.util.MatrixStringUtil.findLongestManagerList;
import static com.artemis.util.MatrixStringUtil.findLongestSystemList;
import static com.artemis.util.MatrixStringUtil.shortName;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.artemis.model.scan.ArtemisTypeData;
import com.artemis.model.scan.ConfigurationResolver;
import com.artemis.util.ClassFinder;
import com.x5.template.Chunk;
import com.x5.template.Theme;

public class ComponentDependencyMatrix implements Opcodes  {
	private final File root;
	private final File output;
	private final String projectName;
	private final ConfigurationResolver scanner;
	
	public ComponentDependencyMatrix(String projectName, File root, File output) {
		this.projectName = projectName;
		this.root = root;
		this.output = output;
		this.scanner = new ConfigurationResolver(root);
	}
	
	public void process() {
		if (scanner.components.size() == 0
			&& scanner.systems.size() == 0
			&& scanner.managers.size() == 0) {
			
			String error = "No artemis classes found on classpath. "
				+ "See https://github.com/junkdog/artemis-odb/wiki/Component-Dependency-Matrix for more info.";
			throw new RuntimeException(error);
		}
		
		List<ArtemisTypeData> artemisTypes = findArtemisTypes(root);
		if (artemisTypes.size() == 0)
			return;
		
		// TODO: move to ColumnIndexMapping
		SortedSet<Type> componentSet = findComponents(artemisTypes);
		
		// removes any artemis classes which aren't part of artemis
		scanner.clearNonDefaultTypes();
		
		List<ArtemisTypeMapping> typeMappings = new ArrayList<ArtemisTypeMapping>();
		for (ArtemisTypeData system : artemisTypes) {
			ArtemisTypeMapping mappedType = ArtemisTypeMapping.from(
				system, scanner, getComponentIndices(componentSet)); // TODO: move to outside loop
			typeMappings.add(mappedType);
		}
		

		List<String> componentColumns = new ArrayList<String>();
		for (Type component : componentSet) {
			String name = component.getClassName();
			name = name.substring(name.lastIndexOf('.') + 1);
			componentColumns.add(name);
		}
		
		ColumnIndexMapping columnIndexMap = new ColumnIndexMapping(componentColumns, typeMappings);
		for (ArtemisTypeMapping typeMapping : typeMappings) {
			typeMapping.setArtemisTypeIndicies(columnIndexMap);
		}
		
//		write(toMap(typeMappings), componentColumns);
		write(toMap(typeMappings), columnIndexMap);
	}
	
	public static SortedMap<String,List<ArtemisTypeMapping>> toMap(List<ArtemisTypeMapping> systems) {
		String common = findCommonPackage(systems);
		SortedMap<String, List<ArtemisTypeMapping>> map = new TreeMap<String, List<ArtemisTypeMapping>>();
		for (int i = 0, s = systems.size(); s > i; i++) {
			ArtemisTypeMapping system = systems.get(i);
			String packageName = toPackageName(system.artemisType.getClassName());
			packageName = (packageName.length() > common.length())
				? packageName.substring(common.length())
				: ".";
			if (!map.containsKey(packageName))
				map.put(packageName, new ArrayList<ArtemisTypeMapping>());
			
			map.get(packageName).add(system);
		}
		
		return map;
	}
	
	private static String findCommonPackage(List<ArtemisTypeMapping> systems) {
		String prefix = toPackageName(systems.get(0).artemisType.getClassName());
		for (int i = 1, s = systems.size(); s > i; i++) {
			String p = toPackageName(systems.get(i).artemisType.getClassName());
			for (int j = 0, l = Math.min(prefix.length(), p.length()); l > j; j++) {
				if (prefix.charAt(j) != p.charAt(j)) {
					prefix = prefix.substring(0, j);
					break;
				}
			}
		}

		return prefix;
	}

	private static String toPackageName(String className) {
		return className.substring(0, className.lastIndexOf('.'));
	}

	private List<ArtemisTypeData> findArtemisTypes(File root) {
		List<ArtemisTypeData> systems = new ArrayList<ArtemisTypeData>();
		for (File f : ClassFinder.find(root))
			inspectType(f, systems);
		
		Collections.sort(systems, new TypeComparator());
		return systems;
	}

	private static SortedSet<Type> findComponents(List<ArtemisTypeData> artemisTypes) {
		SortedSet<Type> componentSet = new TreeSet<Type>(new ShortNameComparator());
		for (ArtemisTypeData artemis : artemisTypes) {
			componentSet.addAll(artemis.requires);
			componentSet.addAll(artemis.requiresOne);
			componentSet.addAll(artemis.optional);
			componentSet.addAll(artemis.exclude);
		}
		return componentSet;
	}
	
	private void write(SortedMap<String, List<ArtemisTypeMapping>> mappedSystems, ColumnIndexMapping columnIndices) {
		Theme theme = new Theme();
		Chunk chunk = theme.makeChunk("matrix");
		
		List<ArtemisTypeMapping> mapping = new ArrayList<ArtemisTypeMapping>();
		for (Entry<String,List<ArtemisTypeMapping>> entry : mappedSystems.entrySet()) {
			mapping.add(new ArtemisTypeMapping(entry.getKey()));
			mapping.addAll(entry.getValue());
		}
		
		chunk.set("longestName", findLongestClassName(mappedSystems).replaceAll(".", "_") + "______");
		
		chunk.set("systems", mapping);
		chunk.set("headersComponents", columnIndices.componentColumns);
		chunk.set("componentCount", columnIndices.componentColumns.size());
		chunk.set("headersManagers", columnIndices.managerColumns);
		chunk.set("managerCount", columnIndices.managerColumns.size());
		chunk.set("headersSystems", columnIndices.systemColumns);
		chunk.set("systemCount", columnIndices.systemColumns.size());
		chunk.set("project", projectName);
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(output));
			chunk.render(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) try {
				out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static Map<Type,Integer> getComponentIndices(SortedSet<Type> componentSet) {
		Map<Type, Integer> componentIndices = new HashMap<Type, Integer>();
		int index = 0;
		for (Type component : componentSet) {
			componentIndices.put(component, index++);
		}
		return componentIndices;
	}
	
	private void inspectType(File file, List<ArtemisTypeData> destination) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			ClassReader cr = new ClassReader(stream);
			Type objectType = Type.getObjectType(cr.getClassName());
			if (!(scanner.managers.contains(objectType) || scanner.systems.contains(objectType)))
				return;
			
			ArtemisTypeData meta = scanner.scan(cr);
			meta.current = objectType;
			destination.add(meta);
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
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
	
	private static class ShortNameComparator implements Comparator<Type> {
		@Override
		public int compare(Type o1, Type o2) {
			return shortName(o1).compareTo(shortName(o2));
		}
	}

	private static class TypeComparator implements Comparator<ArtemisTypeData> {
		@Override
		public int compare(ArtemisTypeData o1, ArtemisTypeData o2) {
			return o1.current.toString().compareTo(o2.current.toString());
		}
	}
	
	static class ColumnIndexMapping {
		private final List<String> componentColumns;
		private final List<String> managerColumns;
		private final List<String> systemColumns;
		final Map<String, Integer> managerIndexMap;
		final Map<String, Integer> systemIndexMap;
		
		ColumnIndexMapping(List<String> componentColumns, List<ArtemisTypeMapping> typeMappings) {
			this.componentColumns = new ArrayList<String>(componentColumns);
			managerColumns = new ArrayList<String>();
			systemColumns = new ArrayList<String>();
			managerIndexMap = new HashMap<String,Integer>();
			systemIndexMap = new HashMap<String,Integer>();
			extractArtemisTypes(typeMappings);
		}
		
		private void extractArtemisTypes(List<ArtemisTypeMapping> typeMappings) {
			SortedSet<String> referencedManagers = new TreeSet<String>();
			SortedSet<String> referencedSystems = new TreeSet<String>();
			
			for (ArtemisTypeMapping mapping : typeMappings) {
				insert(referencedManagers, mapping.refManagers);
				insert(referencedSystems, mapping.refSystems);
			}
			
			int nextColumnIndex = 0;
			for (String manager : referencedManagers) {
				managerIndexMap.put(manager, nextColumnIndex++);
			}
			
			nextColumnIndex = 0;
			for (String system : referencedSystems) {
				systemIndexMap.put(system, nextColumnIndex++);
			}
			
			managerColumns.addAll(referencedManagers);
			systemColumns.addAll(referencedSystems);
		}

		private static void insert(SortedSet<String> artemisSet, String[] referenced) {
			for (String ref : referenced)
				artemisSet.add(ref);
		}
	}
}
