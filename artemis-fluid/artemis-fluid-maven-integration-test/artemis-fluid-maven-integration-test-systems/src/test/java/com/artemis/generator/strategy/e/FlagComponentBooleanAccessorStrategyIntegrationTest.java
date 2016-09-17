package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.component.Basic;
import org.junit.Assert;
import org.junit.Test;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class FlagComponentBooleanAccessorStrategyIntegrationTest extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_toggle_flag_component_Should_affect_component_and_return_fluid() throws Exception {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                E e = E();
                Assert.assertTrue(e.flag(true).isFlag());
                Assert.assertFalse(e.flag(false).isFlag());
            }
        }

        runFluidWorld(new TestSystem());
    }

}
