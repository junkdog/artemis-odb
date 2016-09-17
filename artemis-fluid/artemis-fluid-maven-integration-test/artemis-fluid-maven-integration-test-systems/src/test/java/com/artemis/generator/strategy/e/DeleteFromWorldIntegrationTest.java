package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.E;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class DeleteFromWorldIntegrationTest extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_deleteFromWorld_Should_delete_from_world() throws Exception {

        class TestSystem extends BaseSystem {
            public int entityId;

            @Override
            protected void processSystem() {
                entityId = world.create();
                E.E(entityId).deleteFromWorld();
            }
        }

        class TestSystem2 extends BaseSystem {

            TestSystem testSystem;

            @Override
            protected void processSystem() {
                Assert.assertFalse(world.getEntityManager().isActive(testSystem.entityId));
            }
        }

        runFluidWorld(new TestSystem(), new TestSystem2());
    }
}
