package com.artemis;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daan van Yperen
 */
public class EntityLifecycleListenerTest {

    @Test
    public void When_no_register_registered_Should_not_explode() {
        World w = new World();
        int i = w.create();
        w.delete(i);
    }

    @Test
    public void When_listener_registered_Should_intercept_int_calls() {
        final MyListenerSystem system = new MyListenerSystem();
        final World w = run(system);
        int id = w.create();
        w.delete(id);
        Assert.assertEquals(1,system.created);
        Assert.assertEquals(1,system.deleted);
    }

    @Test
    public void When_lifecycle_events_in_system_initialization_Should_still_callback() {
        final MyListenerSystem system = new MyListenerSystem() {
            @Override
            protected void initialize() {
                int id = world.create();
                world.getEntity(id);
                world.delete(id);
            }
        };
        final World w = run(system);
        Assert.assertEquals(1,system.created);
        Assert.assertEquals(1,system.deleted);
    }

    @Test
    public void When_two_listener_registered_Should_callback_on_both() {
        final MyListenerSystem system = new MyListenerSystem();
        final MyListenerSystem system2 = new MyListenerSystem2();
        final World w = run(system, system2);

         w.create();

        Assert.assertEquals(1,system.created);
        Assert.assertEquals(1,system2.created);
    }


    @Test
    public void When_listener_registered_Should_intercept_entity_calls() {
        final MyListenerSystem system = new MyListenerSystem();
        final World w = run(system);

        final Entity e = w.createEntity();
        e.deleteFromWorld();

        final Entity e2 = w.createEntity();
        w.deleteEntity(e2);

        Assert.assertEquals(2,system.created);
        Assert.assertEquals(2,system.deleted);
    }

    private World run(MyListenerSystem ... systems) {
        final WorldConfiguration c = new WorldConfiguration();
        for (MyListenerSystem system : systems) {
            c.setSystem(system);
        }
        return new World(c);
    }


    public static class MyListenerSystem2 extends MyListenerSystem {
    }

    public static class MyListenerSystem extends BaseSystem implements EntityLifecycleListener {

        int deleted;
        int created;

        @Override
        public void onEntityDeleteIssued(int entityId) {
            deleted++;
        }

        @Override
        public void onEntityCreated(int entityId) {
            created++;
        }

        @Override
        protected void processSystem() {
        }
    }

    ;
}
