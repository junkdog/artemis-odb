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
		
		int e = shipFactory.create();
		int e2 = shipFactory.create();
		
		// 1 is an entity with zero components.
		assertEquals(2, EntityHelper.getCompositionId(w, e));
		assertEquals(2, EntityHelper.getCompositionId(w, e2));
		
		assertNotEquals(e, e2);
	}
	
	@Test
	public void test_sticky_and_per_instance() {
		World world = new World();

		Ship shipFactory = world.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
		
		shipFactory.hitPoints(100);
		
		int e1 = shipFactory.size(10, 20).create();
		int e2 = shipFactory.create();
		
		assertEquals(10, EntityHelper.getComponent(Size.class, world, e1).width, 0.001f);
		assertEquals(20, EntityHelper.getComponent(Size.class, world, e1).height, 0.001f);
		assertEquals(100, EntityHelper.getComponent(HitPoints.class, world, e1).hitpoints);
		assertEquals(0, EntityHelper.getComponent(Size.class, world, e2).width, 0.001f);
		assertEquals(0, EntityHelper.getComponent(Size.class, world, e2).height, 0.001f);
		assertEquals(100, EntityHelper.getComponent(HitPoints.class, world, e2).hitpoints);
	}
	
	@Test
	public void test_update_sticky() {
		World w = new World();

		Ship shipFactory = w.createFactory(Ship.class);
		int e1 = shipFactory.hitPoints(100).create();
		int e2 = shipFactory.copy().hitPoints(200).create();
		
		assertEquals(100, EntityHelper.getComponent(HitPoints.class, w, e1).hitpoints);
		assertEquals(200, EntityHelper.getComponent(HitPoints.class, w, e2).hitpoints);
	}
	
	@Test(expected=RuntimeException.class)
	public void test_fail_on_sticky_update_after_creation() {
		World w = new World();

		Ship shipFactory = w.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
		
		shipFactory.hitPoints(100).create();
		shipFactory.hitPoints(200).create();
	}
	
	@Test
	public void test_fluent_api_test() {
		World w = new World();

		Ship fac = w.createFactory(Ship.class);
		fac.hitPoints(20).tag("hello").size(20, 10);
		fac.hitPoints(20).group("hello").size(20, 10).tag("hello");
	}
}
