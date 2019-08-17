package com.artemis;

import com.artemis.annotations.All;
import com.artemis.common.NotExplicitlyDelayedComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class DelayedComponentRemovalTest {

    public static final int BASE_SYSTEM_COUNT = 3;
    private WorldConfigurationBuilder builder;

    @Before
    public void setUp() {
        builder = new WorldConfigurationBuilder();
    }

    @Test
    public void Should_apply_flags_on_config() {
        WorldConfiguration configuration = builder.alwaysDelayComponentRemoval(true).build();
        Assert.assertTrue(configuration.isAlwaysDelayComponentRemoval());
    }

    @Test
    public void Should_apply_flags_on_world() {
        World world = new World(builder.alwaysDelayComponentRemoval(true).build());
        Assert.assertTrue(world.isAlwaysDelayComponentRemoval());
    }

    @Test
    public void When_active_Should_delay_component_removal() {
        DelayedRemovalTestSystem delayedRemovalTestSystem = new DelayedRemovalTestSystem();
        World world = new World(builder.with(delayedRemovalTestSystem).alwaysDelayComponentRemoval(true).build());
        world.process();
        Assert.assertFalse(delayedRemovalTestSystem.hasAtRemoval);
        Assert.assertTrue(delayedRemovalTestSystem.gettableAtRemoval);
    }


    @Test
    public void When_inactive_Should_not_delay_component_removal() {
        DelayedRemovalTestSystem delayedRemovalTestSystem = new DelayedRemovalTestSystem();
        World world = new World(builder.with(delayedRemovalTestSystem).alwaysDelayComponentRemoval(false).build());
        world.process();
        Assert.assertFalse(delayedRemovalTestSystem.hasAtRemoval);
        Assert.assertFalse(delayedRemovalTestSystem.gettableAtRemoval);
    }

    @All(NotExplicitlyDelayedComponent.class)
    private static class DelayedRemovalTestSystem extends BaseEntitySystem {

        boolean gettableAtRemoval = false;
        private boolean hasAtRemoval = false;
        ComponentMapper<NotExplicitlyDelayedComponent> m;
        private Entity e;

        @Override
        protected void initialize() {
            super.initialize(); // setup world
            e = world.createEntity();
            m.create(e);
        }

        @Override
        protected void removed(int entityId) {
            super.removed(entityId);
            hasAtRemoval = m.has(entityId);
            gettableAtRemoval = m.get(entityId) != null;
        }

        @Override
        protected void processSystem() {
            m.remove(e); // remove on next process. should trigger removed when done processing.
        }
    }
}
