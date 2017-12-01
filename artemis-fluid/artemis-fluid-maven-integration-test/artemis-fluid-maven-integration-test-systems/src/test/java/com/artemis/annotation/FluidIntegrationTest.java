package com.artemis.annotation;

import com.artemis.CosplayBaseSystem;
import com.artemis.E;
import com.artemis.generator.strategy.e.AbstractStrategyIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class FluidIntegrationTest extends AbstractStrategyIntegrationTest {

    @Test
    public void When_excluding_component_Should_exclude_component() throws Exception {

        class TestSystem extends CosplayBaseSystem<E> {
            @Override
            protected void processSystem() {
                E e = E();
                try {
                    e.getClass().getMethod("hasExcluded");
                    Assert.fail();
                } catch (NoSuchMethodException ignore) {
                    // do nothing
                }
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_specified_name_Should_apply_name_on_component() throws Exception {

        class TestSystem extends CosplayBaseSystem<E> {
            @Override
            protected void processSystem() {
                E e = E();
                try {
                    e.getClass().getMethod("hasRename2");
                } catch (NoSuchMethodException ignore) {
                    Assert.fail();
                }
            }
        }

        runFluidWorld(new TestSystem());
    }

    @Test
    public void When_swallowing_parameterized_getters_Should_return_fluid_instead_of_parameterized_getter_return_value() throws Exception {

        class TestSystem extends CosplayBaseSystem<E> {
            @Override
            protected void processSystem() {
                E e = E();
                Assert.assertEquals(e, e.paraGetterCustom("test2"));
            }
        }

        runFluidWorld(new TestSystem());
    }


}
