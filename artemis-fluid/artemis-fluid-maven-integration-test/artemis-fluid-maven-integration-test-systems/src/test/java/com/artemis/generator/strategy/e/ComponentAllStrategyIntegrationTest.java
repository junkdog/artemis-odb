package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.component.Basic;
import com.artemis.managers.TagManager;
import org.junit.Assert;
import org.junit.Test;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class ComponentAllStrategyIntegrationTest extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_create_component_Should_create_component_and_return_fluid() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertTrue(E().basic().hasBasic());
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_fluid_remove_component_Should_remove_component_and_return_fluid() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertNull(E().basic().removeBasic().getBasic());
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_fluid_fetch_component_Should_return_component() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Basic basic = E().basic().getBasic();
                Assert.assertNotNull(basic);
                Assert.assertTrue(Basic.class.equals(basic.getClass()));
            }
        }

        runFluidWorld(new TestSystem());
    }


}
