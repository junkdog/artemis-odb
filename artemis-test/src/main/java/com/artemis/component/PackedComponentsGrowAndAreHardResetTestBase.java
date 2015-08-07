package com.artemis.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;

public class PackedComponentsGrowAndAreHardResetTestBase {

	private World world;

	@Before
	public void setup() {
		world = new World();
	}
	
	private void create() {
		Entity e = world.createEntity();
		e.edit().create(SimpleComponent.class).value = e.getId();
	}
	
	@Test
	public void values_survive_grow_2256() {
		check(2512);
	}
	
	@Test
	public void values_survive_grow_256() {
		check(256);
	}
	
	@Test
	public void values_survive_grow_1024() {
		check(1024);
	}

	private void check(int total) {
			for (int i = 0; total > i; i++) try {
				create();
			} catch (IndexOutOfBoundsException e) {
				fail("failed at index " + i);
			}
		
		world.process();
		
		ComponentMapper<SimpleComponent> mapper = world.getMapper(SimpleComponent.class);
		assertNotNull(mapper);
		for (int i = 0; total > i; i++) {
			Entity e = world.getEntity(i);
			assertEquals(i, mapper.get(e).value);
		}
		
	}
}
