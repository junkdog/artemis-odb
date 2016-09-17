package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.component.Basic;
import org.junit.Assert;
import org.junit.Test;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public class ComponentFieldAccessorStrategyIntegrationTest extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_setget_component_field_Should_setget_component_field_value() throws Exception {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals(5,E().basicX(5).basicX());
                Basic b = new Basic();
                Assert.assertEquals(b,E().basicO(b).basicO());
                Assert.assertEquals("test",E().basicS("test").basicS());
            }
        }

        runFluidWorld(new TestSystem());
    }

}
