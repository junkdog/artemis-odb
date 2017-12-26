package com.artemis.generator.strategy.e;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class ComponentGroupStrategyIntegrationTest extends AbstractStrategyIntegrationTest {

    @Test
    public void When_fluid_set_group_Should_set_group() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Entity entity = E.E().group("test").entity();
                Assert.assertTrue(groupManager.isInGroup(entity, "test"));
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }

    @Test
    public void When_fluid_set_groups_Should_set_groups() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Entity entity = E.E().groups("a","b").entity();
                Assert.assertTrue(groupManager.isInGroup(entity, "a"));
                Assert.assertTrue(groupManager.isInGroup(entity, "b"));
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }

    @Test
    public void When_fluid_remove_group_Should_remove_group() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Entity entity = E.E().groups("a","b", "c").removeGroup("b").entity();
                Assert.assertTrue(groupManager.isInGroup(entity, "a"));
                Assert.assertFalse(groupManager.isInGroup(entity, "b"));
                Assert.assertTrue(groupManager.isInGroup(entity, "c"));
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }


    @Test
    public void When_fluid_remove_groups_Should_remove_groups() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Entity entity = E.E().groups("a","b", "c").removeGroups("b", "c").entity();
                Assert.assertTrue(groupManager.isInGroup(entity, "a"));
                Assert.assertFalse(groupManager.isInGroup(entity, "b"));
                Assert.assertFalse(groupManager.isInGroup(entity, "c"));
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }


    @Test
    public void When_fluid_remove_all_groups_Should_remove_all_groups() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Entity entity = E.E().groups("a","b", "c").removeGroups().entity();
                Assert.assertFalse(groupManager.isInGroup(entity, "a"));
                Assert.assertFalse(groupManager.isInGroup(entity, "b"));
                Assert.assertFalse(groupManager.isInGroup(entity, "c"));
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }


    @Test
    public void When_fluid_check_in_group_Should_report_group_membership() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Assert.assertFalse(E.E().group("a").isInGroup("b"));
                Assert.assertTrue(E.E().group("a").isInGroup("a"));
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }


    @Test
    public void When_fluid_check_groups_Should_return_groups() throws Exception {

        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {

                Assert.assertEquals(
                        3, E.E().groups("a","b","c").groups().size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }



    @Test
    public void When_fetching_entities_in_group_Should_properly_resolve_group_members() throws Exception {
        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                for (int i = 0; i < 10; i++) {
                    E.E().groups("a");
                }
                Assert.assertEquals(10,E.withGroup("a").size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }




    @Test
    public void When_fetching_entities_in_empty_group_Should_return_empty_group() throws Exception {
        class TestSystem extends BaseSystem {
            public GroupManager groupManager;
            @Override
            protected void processSystem() {
                Assert.assertEquals(0,E.withGroup("a").size());
            }
        }

        runFluidWorld(new TestSystem(), new GroupManager());
    }
}
