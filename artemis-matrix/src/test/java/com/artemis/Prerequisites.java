package com.artemis;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.artemis.util.CollectionsUtil;
import com.google.common.base.Predicate;

public class Prerequisites {
	
	@Test
	public void findAllSystems() {
		Reflections reflections = new Reflections("com.artemis");
		Set<Class<? extends EntitySystem>> systems = reflections.getSubTypesOf(EntitySystem.class);
		systems = CollectionsUtil.filter(systems, "com.artemis.matrix.system");
		System.out.println(systems);
	}
}
