package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.CosplayBaseSystem;
import com.artemis.E;
import com.artemis.managers.GroupManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class DeleteFromWorldIntegrationTest extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_deleteFromWorld_Should_delete_from_world() throws Exception {

        class TestSystem extends CosplayBaseSystem<E> {
            public int entityId;

            @Override
            protected void processSystem() {
                entityId = world.create();
                E(entityId).group("test").deleteFromWorld();
            }
        }

        class TestSystem2 extends BaseSystem {
            public int entityId;

            GroupManager groupManager;

            @Override
            protected void processSystem() {
                Assert.assertTrue(groupManager.getEntityIds("test").isEmpty());
            }
        }

        runFluidWorld(new GroupManager(), new TestSystem(), new TestSystem2());
    }
}
