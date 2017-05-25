package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ComponentTagStrategyIntegrationTest extends AbstractStrategyIntegrationTest {

    @Test
    public void When_fluid_set_tag_Should_set_tag() throws Exception {

        class TestSystem extends BaseSystem {
            public TagManager tagManager;
            @Override
            protected void processSystem() {
                int entity = E.E().tag("test").id();
                Assert.assertEquals("test",tagManager.getTag(entity));
            }
        }

        runFluidWorld(new TestSystem(), new TagManager());
    }


    @Test
    public void When_fluid_get_tag_Should_get_tag() throws Exception {

        class TestSystem extends BaseSystem {
            @Override
            protected void processSystem() {
                Assert.assertEquals("test",E.E().tag("test").tag());
            }
        }

        runFluidWorld(new TestSystem(), new TagManager());
    }
}
