package com.artemis;

import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Daan van Yperen
 */
public class EntityEditPoolTest {

	@Test
	public void test_flyweight_of_newly_created_entity_supported_in_transmuter() {
		class TestSystem extends BaseSystem {

			private EntityTransmuter createTransmuter;

			@Override
			protected void initialize() {
				createTransmuter = new EntityTransmuterFactory(world).add(ComponentX.class).build();
			}

			@Override
			protected void processSystem() {
				final int entity = world.createEntity();
				createTransmuter.transmute(entity);
			}
		}
		new World(new WorldConfiguration().setSystem(new TestSystem())).process();
		// success if no runtimeException.
	}

}