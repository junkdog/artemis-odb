package com.artemis;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.artemis.ParamArchTest.HitPoints;
import com.artemis.ParamArchTest.Size;
import com.artemis.reference.Ship;
import com.artemis.reference.ShipImpl;

@SuppressWarnings("static-method")
public class EntityFactoryTest {
	
	@Test
	public void test_resolving_entity_factory() {
		World w = new World();

		Ship shipFactory = w.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
		
		Entity e = shipFactory.create();
		Entity e2 = shipFactory.create();
		
		// 1 is an entity with zero components.
		assertEquals(2, e.getCompositionId());
		assertEquals(2, e2.getCompositionId());
		
		assertNotEquals(e.getId(), e2.getId());
	}
	
	@Test
	public void test_sticky_and_per_instance() {
		World w = new World();
		w.initialize();
		
		Ship shipFactory = w.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
		
		shipFactory.hitPoints(100);
		
		Entity e1 = shipFactory.size(10, 20).create();
		Entity e2 = shipFactory.create();
		
		assertEquals(10, e1.getComponent(Size.class).width, 0.001f);
		assertEquals(20, e1.getComponent(Size.class).height, 0.001f);
		assertEquals(100, e1.getComponent(HitPoints.class).hitpoints);
		assertEquals(0, e2.getComponent(Size.class).width, 0.001f);
		assertEquals(0, e2.getComponent(Size.class).height, 0.001f);
		assertEquals(100, e2.getComponent(HitPoints.class).hitpoints);
	}
	
	@Test
	public void test_update_sticky() {
		World w = new World();
		w.initialize();
		
		Ship shipFactory = w.createFactory(Ship.class);
		Entity e1 = shipFactory.hitPoints(100).create();
		Entity e2 = shipFactory.copy().hitPoints(200).create();
		
		assertEquals(100, e1.getComponent(HitPoints.class).hitpoints);
		assertEquals(200, e2.getComponent(HitPoints.class).hitpoints);
	}
	
	@Test(expected=RuntimeException.class)
	public void test_fail_on_sticky_update_after_creation() {
		World w = new World();
		w.initialize();
		
		Ship shipFactory = w.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
		
		shipFactory.hitPoints(100).create();
		shipFactory.hitPoints(200).create();
	}
	
	@Test
	public void test_fluent_api_test() {
		World w = new World();
		w.initialize();
		
		Ship fac = w.createFactory(Ship.class);
		fac.hitPoints(20).tag("hello").size(20, 10);
		fac.hitPoints(20).group("hello").size(20, 10).tag("hello");
	}
}
