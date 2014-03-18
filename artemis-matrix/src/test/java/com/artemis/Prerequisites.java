package com.artemis;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;

import com.artemis.util.CollectionsUtil;

public class Prerequisites {
	
	@Test
	public void findAllSystems() {
		Reflections reflections = new Reflections("com.artemis");
		Set<Class<? extends EntitySystem>> systems = reflections.getSubTypesOf(EntitySystem.class);
		systems = CollectionsUtil.filter(systems, "com.artemis.matrix.system");
		Assert.assertEquals(2, systems.size());
	}
	
	@Test
	public void findComponents() {
		Reflections reflections = new Reflections("com.artemis");
		Set<Class<? extends Component>> components = reflections.getSubTypesOf(Component.class);
		components = CollectionsUtil.filter(components, "com.artemis.component");
		Assert.assertTrue(components.size() > 0);
	}
}
