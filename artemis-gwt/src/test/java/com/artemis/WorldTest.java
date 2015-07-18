package com.artemis;


import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.system.ExpirationSystem;
import com.artemis.system.SystemB;
import com.artemis.system.SystemComponentXRemover;
import com.google.gwt.junit.client.GWTTestCase;

public class WorldTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	private World world;

	public void test_get_component_should_not_throw_exception() {
		world = new World();

		for (int i = 0; i < 100; i++) {
			Entity e = world.createEntity();
			if (i == 0) e.edit().add(new ComponentX());
		}

		world.process();

		for (int i = 0; i < 100; i++) {
			Entity e = world.getEntity(i);
			e.getComponent(ComponentX.class);
		}
	}

	public void test_access_component_after_deletion_in_previous_system() {
		world = new World(new WorldConfiguration()
				.setSystem(new SystemComponentXRemover())
				.setSystem(new SystemB()));

		Entity e = world.createEntity();
		e.edit().create(ComponentX.class);
		
		world.process();
	}
	
	public void test_delayed_entity_procesing_ensure_entities_processed() {
		ExpirationSystem es = new ExpirationSystem();
		world = new World(new WorldConfiguration()
			.setSystem(es));

		Entity e1 = createEntity();
		
		world.setDelta(0.5f);
		world.process();
		assertEquals(0, es.expiredLastRound);
		
		Entity e2 = createEntity();
		
		world.setDelta(0.75f);
		world.process();
		assertEquals(1, es.expiredLastRound);
		assertEquals(0.25f, es.deltas.get(e2.getId()), 0.01f);
		world.delta = 0;
		world.process();
		assertEquals(1, es.getActives().size());
		
		world.setDelta(0.5f);
		world.process();
		
		assertEquals(1, es.expiredLastRound);
		
		world.process();
		assertEquals(0, es.getActives().size());
	}

	private Entity createEntity() {
		Entity e = world.createEntity();
		e.edit().create(ComponentY.class);
		return e;
	}
}
