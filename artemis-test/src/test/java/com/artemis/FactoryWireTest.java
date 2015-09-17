package com.artemis;

import static org.junit.Assert.*;

import org.junit.Test;

import com.artemis.factory.Ship;
import com.artemis.factory.ShipNoMethods;
import com.artemis.factory.ShipShortWire;

@SuppressWarnings("static-method")
public class FactoryWireTest {
	
	@Test
	public void test_inject_entity_factories() {
		Man man = new Man();
		World w = new World(new WorldConfiguration()
				.setSystem(man));

		assertNotNull(man.ship);
		assertEquals(Ship.class.getName() + "Impl", man.ship.getClass().getName());
		assertNotNull(man.shipNoMethods);
		assertEquals(ShipNoMethods.class.getName() + "Impl",
				man.shipNoMethods.getClass().getName());
		assertNotNull(man.shipShortWire);
		assertEquals(ShipShortWire.class.getName() + "Impl",
				man.shipShortWire.getClass().getName());
	}
	
	private static class Man extends Manager {
		private Ship ship;
		private ShipNoMethods shipNoMethods;
		private ShipShortWire shipShortWire;
	}
}
