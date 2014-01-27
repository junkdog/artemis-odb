package com.artemis;

import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ComponentFinder {
	
	private ComponentFinder() {}
	
	static Set<Class<? extends Component>> getComponents(URL path, String... packages) {
		
		ConfigurationBuilder config = new ConfigurationBuilder();
		for (String p : packages)
			config.setUrls(ClasspathHelper.forPackage(p));
		
		Reflections reflections = new Reflections(config);
		Set<Class<? extends Component>> components = new TreeSet<Class<? extends Component>>(new ClassNameComparator());
		components.addAll(reflections.getSubTypesOf(Component.class));
		components.addAll(reflections.getSubTypesOf(PooledComponent.class));
		components.addAll(reflections.getSubTypesOf(PackedComponent.class));
		
		return components;
	}
	
	private static final class ClassNameComparator implements Comparator<Class<? extends Component>> {
		@Override
		public int compare(Class<? extends Component> c1, Class<? extends Component> c2) {
			return c1.getSimpleName().compareTo(c2.getSimpleName());
		}
	}
}
