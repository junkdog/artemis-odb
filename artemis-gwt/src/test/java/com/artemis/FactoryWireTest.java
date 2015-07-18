package com.artemis;


import com.artemis.factory.Ship;
import com.artemis.factory.ShipNoMethods;
import com.artemis.factory.ShipShortWire;
import com.artemis.manager.TypedEntityFactory;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("static-method")
public class FactoryWireTest extends GWTTestCase {
	
	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}
	
	public void test_inject_entity_factories() {
		TypedEntityFactory man = new TypedEntityFactory();
		World w = new World(new WorldConfiguration()
				.setManager(man));
		
		assertNotNull(man.ship);
		assertEquals(Ship.class.getName() + "Impl", man.ship.getClass().getName());
		assertNotNull(man.shipNoMethods);
		assertEquals(ShipNoMethods.class.getName() + "Impl",
				man.shipNoMethods.getClass().getName());
		assertNotNull(man.shipShortWire);
		assertEquals(ShipShortWire.class.getName() + "Impl",
				man.shipShortWire.getClass().getName());
	}
}
