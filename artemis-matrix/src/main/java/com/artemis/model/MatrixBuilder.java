package com.artemis.model;

import static com.artemis.util.MatrixStringUtil.findLongestClassName;
import static com.artemis.util.MatrixStringUtil.findLongestManagerList;
import static com.artemis.util.MatrixStringUtil.findLongestSystemList;

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

public class MatrixBuilder implements Opcodes  {
	private final File root;
	private final File output;
	private final String projectName;
	private final ConfigurationResolver scanner;
	
	public MatrixBuilder(String projectName, File root, File output) {
		this.projectName = projectName;
		this.root = root;
		this.output = output;
		this.scanner = new ConfigurationResolver("com.github.junkdog.shamans");
	}
	
	public void process() {
		List<ArtemisTypeData> systems = findSystems(root);
		if (systems.size() == 0)
			return;
		SortedSet<Type> componentSet = findComponents(systems);
		
		List<ArtemisMapping> systemMappings = new ArrayList<ArtemisMapping>();
		for (ArtemisTypeData system : systems) {
			ArtemisMapping mappedSystem = ArtemisMapping.from(
				system, getComponentIndices(componentSet));
			systemMappings.add(mappedSystem);
		}
		

		List<String> columns = new ArrayList<String>();
		for (Type component : componentSet) {
			String name = component.getClassName();
			name = name.substring(name.lastIndexOf('.') + 1);
			columns.add(name);
		}
		
		write(toMap(systemMappings), columns);
	}
	
	public static SortedMap<String,List<ArtemisMapping>> toMap(List<ArtemisMapping> systems) {
		String common = findCommonPackage(systems);
		SortedMap<String, List<ArtemisMapping>> map = new TreeMap<String, List<ArtemisMapping>>();
		for (int i = 0, s = systems.size(); s > i; i++) {
			ArtemisMapping system = systems.get(i);
			String packageName = toPackageName(system.system.getClassName());
			packageName = (packageName.length() > common.length())
				? packageName.substring(common.length())
				: ".";
			if (!map.containsKey(packageName))
				map.put(packageName, new ArrayList<ArtemisMapping>());
			
			map.get(packageName).add(system);
		}
		
		return map;
	}
	
	private static String findCommonPackage(List<ArtemisMapping> systems) {
		String prefix = toPackageName(systems.get(0).system.getClassName());
		for (int i = 1, s = systems.size(); s > i; i++) {
			String p = toPackageName(systems.get(i).system.getClassName());
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

	private List<ArtemisTypeData> findSystems(File root) {
		List<ArtemisTypeData> systems = new ArrayList<ArtemisTypeData>();
		for (File f : ClassFinder.find(root))
			filterSystems(f, systems);
		
		Collections.sort(systems, new SystemComparator());
		return systems;
	}

	private static SortedSet<Type> findComponents(List<ArtemisTypeData> systems) {
		SortedSet<Type> componentSet = new TreeSet<Type>(new ComponentSorter());
		for (ArtemisTypeData system : systems) {
			componentSet.addAll(system.requires);
			componentSet.addAll(system.requiresOne);
			componentSet.addAll(system.optional);
			componentSet.addAll(system.exclude);
		}
		return componentSet;
	}
	
	private void write(SortedMap<String, List<ArtemisMapping>> mappedSystems, List<String> columns) {
		Theme theme = new Theme();
		Chunk chunk = theme.makeChunk("matrix");
		
		List<ArtemisMapping> mapping = new ArrayList<ArtemisMapping>();
		for (Entry<String,List<ArtemisMapping>> entry : mappedSystems.entrySet()) {
			mapping.add(new ArtemisMapping(entry.getKey()));
			mapping.addAll(entry.getValue());
		}
		
		chunk.set("longestName", findLongestClassName(mappedSystems).replaceAll(".", "_") + "______");
		chunk.set("longestManagers", findLongestManagerList(mappedSystems).replaceAll(".", "_"));
		chunk.set("longestSystems", findLongestSystemList(mappedSystems).replaceAll(".", "_"));
		chunk.set("systems", mapping);
		chunk.set("headers", columns);
		chunk.set("project", projectName);
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(output));
			chunk.render(out);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (out != null) try {
				out.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	// TODO: rename method - handles managers and systems
	private void filterSystems(File file, List<ArtemisTypeData> destination) {
		// FIXME: temp hack
		if (!(file.toString().endsWith("System.class") || file.toString().endsWith("Manager.class")))
			return;
		
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			
			ClassReader cr = new ClassReader(stream);

			ArtemisTypeData meta = scanner.scan(cr);
			meta.current = Type.getObjectType(cr.getClassName());
			
//			if (meta.annotationType != null) //FIXME ?
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
	
	private static class ComponentSorter implements Comparator<Type> {
		@Override
		public int compare(Type o1, Type o2) {
			return o1.getClassName().compareTo(o2.getClassName());
		}
	}

	private static class SystemComparator implements Comparator<ArtemisTypeData> {
		@Override
		public int compare(ArtemisTypeData o1, ArtemisTypeData o2) {
			return o1.current.toString().compareTo(o2.current.toString());
		}
	}
	
	// FIXME just for debugging
	public static void main(String[] args) {
		File root = new File("/home/junkdog/opt/dev/git/shamans-weirding-game/core/target/classes");
		File output = new File("/home/junkdog/opt/dev/git/shamans-weirding-game/core/target/matrix.html");
		MatrixBuilder mb = new MatrixBuilder("SWG", root, output);
		mb.process();
	}
}
