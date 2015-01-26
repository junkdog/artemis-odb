package com.artemis;

import com.artemis.model.scan.ConfigurationResolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

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
		assertEquals(resolver.systems.toString(), 2, resolver.systems.size());
	}

	@Test
	public void managerIntrospectionTest() {
		assertEquals(resolver.managers.toString(), 1, resolver.managers.size());
	}

	@Test
	public void componentIntrospectionTest() {
		assertEquals(resolver.components.toString(), 3, resolver.components.size());
	}

	@Test
	public void factoryIntrospectionTest() {
		assertEquals(resolver.factories.toString(), 1, resolver.factories.size());
	}
}
