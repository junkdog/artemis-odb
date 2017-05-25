package com.artemis.generator.strategy.e;

import com.artemis.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class EBaseStrategyIntegrationTest extends AbstractStrategyIntegrationTest {

    @Test
    public void When_instancing_fluid_interface_by_entity_id_Should_create_valid_E() {
        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                int entityId = world.create();
                Assert.assertEquals(entityId, E.E(entityId).id());
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_creating_new_entity_with_fluid_interface_by_entity_Should_create_valid_Entity() {
        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                int id = E.E().id();
                Assert.assertTrue(world.getEntityManager().isActive(id));
            }
        }

        runFluidWorld(new TestSystem());
    }

}
