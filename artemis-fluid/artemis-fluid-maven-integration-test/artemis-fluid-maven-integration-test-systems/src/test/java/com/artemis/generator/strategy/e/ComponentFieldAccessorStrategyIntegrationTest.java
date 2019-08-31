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
public class ComponentFieldAccessorStrategyIntegrationTest extends AbstractStrategyIntegrationTest {


    @Test
    public void When_fluid_setget_component_field_Should_setget_component_field_value() {

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

    @Test
    public void When_fluid_set_method_Should_fluid_expose_method() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals(99,E().basic(99).basicX());
                Basic x = new Basic();
                Assert.assertEquals(x,E().basic(99, x).basicO());
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_fluid_custom_getter_Should_expose_without_fluid_return_value() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals("test",E().basicCustom());
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_fluid_parameterized_getter_Should_expose_without_fluid_return_value_by_default() {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals("test2", E().basicCustom("test2"));
            }
        }

        runFluidWorld(new TestSystem());
    }

}
