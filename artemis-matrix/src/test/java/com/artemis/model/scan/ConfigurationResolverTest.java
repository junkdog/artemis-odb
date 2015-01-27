package com.artemis.model.scan;

import com.artemis.component.ExtPosition;
import com.artemis.component.Position;
import com.artemis.component.Velocity;
import com.artemis.factory.FactoryA;
import com.artemis.manager.SomeManager;
import com.artemis.system.AnotherSystem;
import com.artemis.system.SomeSystem;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.io.File;
import java.util.*;

import static com.artemis.model.scan.TypeConfiguration.type;
import static org.junit.Assert.assertEquals;

public final class ConfigurationResolverTest {
	private static ConfigurationResolver resolver;

	@BeforeClass
	public static void setup() {
		resolver = new ConfigurationResolver(new File("target/test-classes").getAbsoluteFile());
		resolver.clearNonDefaultTypes();
	}

	@Test
	public void systemIntrospectionTest() {
		assertTypes(types(SomeSystem.class, AnotherSystem.class), resolver.systems);
	}

	@Test
	public void managerIntrospectionTest() {
		assertTypes(types(SomeManager.class), resolver.managers);
	}

	@Test
	public void componentIntrospectionTest() {
		assertTypes(types(ExtPosition.class, Position.class, Velocity.class), resolver.components);
	}

	@Test
	public void factoryIntrospectionTest() {
		assertTypes(types((FactoryA.class)), resolver.factories);
	}

	private static void assertTypes(Set<Type> expectedTypes, Set<Type> actualTypes) {
		String message = actualTypes.toString();
		assertEquals(message, expectedTypes.size(), actualTypes.size());
		assertEquals(message, expectedTypes, actualTypes);
	}

	private static Set<Type> types(Class<?>... klazzes) {
		Set<Type> expectedTypes = new HashSet<Type>();
		for (Class<?> klazz : klazzes)
			expectedTypes.add(type(klazz));

		return expectedTypes;
	}

}
