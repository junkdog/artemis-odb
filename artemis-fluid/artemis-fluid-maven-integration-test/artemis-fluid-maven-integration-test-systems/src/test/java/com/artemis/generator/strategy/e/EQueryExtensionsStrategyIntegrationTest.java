package com.artemis.generator.strategy.e;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.component.Basic;
import com.artemis.component.Flag;
import com.artemis.managers.GroupManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class EQueryExtensionsStrategyIntegrationTest extends AbstractStrategyIntegrationTest {

    private static final Aspect.Builder BASIC_FLAG_ASPECT = Aspect.all(Basic.class, Flag.class);

    @Test
    public void When_fetching_entities_by_aspect_Should_return_matching_entities() {
        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                for (int i = 0; i < 3; i++) {
                    E.E().basic().flag();
                }
                Assert.assertEquals(3, E.withAspect(BASIC_FLAG_ASPECT).size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }

    @Test
    public void When_fetching_entities_by_component_Should_return_matching_entities() {
        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                for (int i = 0; i < 3; i++) {
                    E.E().basic();
                }
                Assert.assertEquals(3, E.withComponent(Basic.class).size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());

    }
    @Test
    public void When_no_entities_with_aspect_Should_return_empty_ebag() {
        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals(0, E.withAspect(BASIC_FLAG_ASPECT).size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }

    @Test
    public void When_no_entities_with_component_Should_return_empty_bag() {
        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals(0, E.withComponent(Basic.class).size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }
}
