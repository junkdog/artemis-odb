package com.artemis;


import static org.junit.Assert.*;

import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.reference.Ship;
import com.artemis.reference.ShipImpl;

@SuppressWarnings("static-method")
public class EntityFactoryTest {
	
	@Test(expected=MundaneWireException.class)
	public void test_resolving_entity_factory_before_initialization() {
		World w = new World();
		w.createFactory(Ship.class);
	}
	
	@Test
	public void test_resolving_entity_factory() {
		World w = new World();
		w.initialize();
		
		Ship shipFactory = w.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
		
		Entity e = shipFactory.create();
		Entity e2 = shipFactory.create();
		
		w.process();
		
		// 1 is 
		assertEquals(2, e.getCompositionId());
		assertEquals(2, e2.getCompositionId());
		
		assertNotEquals(e.getId(), e2.getId());
	}
	
	@Test
	public void test_sticky() {
		World w = new World();
		w.initialize();
		
		Ship shipFactory = w.createFactory(Ship.class);
		assertNotNull(shipFactory);
		assertEquals(ShipImpl.class, shipFactory.getClass());
	}
}
