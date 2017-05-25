package com.artemis;

import com.artemis.annotations.SkipWire;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;

import static org.junit.Assert.*;

public class WireTest {

    private World world;

    private MappedSystem mappedSystem;
    private MappedSystemAll mappedSystemAll;
    private ExtendedSystem extendedSystem;
    private MappedManager mappedManager;
    private MappedManagerAll mappedManagerAll;
    private ExtendedManager extendedManager;

    private Entity entity;

    @Before
    public void init() {
        mappedSystem = new MappedSystem();
        mappedSystemAll = new MappedSystemAll();
        extendedSystem = new ExtendedSystem();
        mappedManager = new MappedManager();
        mappedManagerAll = new MappedManagerAll();
        extendedManager = new ExtendedManager();

        world = new World(new WorldConfiguration()
                .setSystem(TagManager.class)
                .setSystem(mappedManager)
                .setSystem(mappedManagerAll)
                .setSystem(extendedManager)
                .setSystem(mappedSystem)
                .setSystem(mappedSystemAll)
                .setSystem(extendedSystem));

        entity = world.createEntity();
        EntityEdit edit = entity.edit();
        edit.create(ComponentX.class);
        edit.create(ComponentY.class);

        world.process();
    }

    @Test
    public void inject_custom_type() {
        CustomInjectedManager injected = new CustomInjectedManager();
        InjectMe injectMe = new InjectMe();

        WorldConfiguration config = new WorldConfiguration()
                .setSystem(injected)
                .register(injectMe);

        World w = new World(config);
        assertSame(injectMe, injected.injectMe);
        assertNull(injected.nullInjectMe);
    }

    @Test(expected = MundaneWireException.class)
    public void inject_custom_type_not_registered() {
        CustomInjectedManager injected = new CustomInjectedManager();
        InjectMe injectMe = new InjectMe();

        WorldConfiguration config = new WorldConfiguration()
                .setSystem(injected);

        World w = new World(config);
    }

    @Test
    public void inject_named_custom_type() {
        CustomNamedInjectedManager injected = new CustomNamedInjectedManager();
        InjectMe injectMe = new InjectMe();

        WorldConfiguration config = new WorldConfiguration()
                .setSystem(injected)
                .register("hi", injectMe);

        World w = new World(config);
        assertSame(injectMe, injected.injectMe);
        assertNull(injected.nullInjectMe);
    }

    @Test
    public void get_registered_types() {
        InjectMe injectMeName = new InjectMe();
        InjectMe injectMeType = new InjectMe();

        WorldConfiguration config = new WorldConfiguration()
                .register("hi", injectMeName)
                .register(injectMeType);

        World w = new World(config);
        assertSame(injectMeName, w.getRegistered("hi"));
        assertSame(injectMeType, w.getRegistered(InjectMe.class));
    }

    @Test(expected = MundaneWireException.class)
    public void inject_named_custom_type_not_registered() {
        CustomNamedInjectedManager injected = new CustomNamedInjectedManager();
        InjectMe injectMe = new InjectMe();

        WorldConfiguration config = new WorldConfiguration()
                .setSystem(injected);

        World w = new World(config);
    }

    @Test
    public void systems_support_wire_annotation() {
        assertNotNull(mappedSystem.x);
        assertNotNull(mappedSystem.y);
        assertNotNull(mappedSystem.tagManager);
        assertNotNull(mappedSystem.mappedSystemAll);
        assertNotNull(extendedSystem.x);
        assertNotNull(extendedSystem.y);

        assertEquals(ComponentX.class, mappedSystem.x.get(entity).getClass());
        assertEquals(ComponentY.class, mappedSystem.y.get(entity).getClass());
    }

    @Test
    public void managers_support_wire_annotation() {
        assertNotNull(mappedManager.x);
        assertNotNull(mappedManager.y);
        assertNotNull(mappedManager.tagManager);
        assertNotNull(mappedManager.mappedSystem);

        assertEquals(ComponentX.class, mappedSystem.x.get(entity).getClass());
        assertEquals(ComponentY.class, mappedSystem.y.get(entity).getClass());
    }

    @Test
    public void systems_all_support_wire_annotation() {
        assertNotNull(mappedSystemAll.x);
        assertNotNull(mappedSystemAll.y);
        assertNotNull(mappedSystemAll.tagManager);
        assertNotNull(mappedSystemAll.mappedSystem);

        assertEquals(ComponentX.class, mappedSystem.x.get(entity).getClass());
        assertEquals(ComponentY.class, mappedSystem.y.get(entity).getClass());
    }

    @Test
    public void managers_all_support_wire_annotation() {
        assertNotNull(mappedManagerAll.x);
        assertNotNull(mappedManagerAll.y);
        assertNotNull(mappedManagerAll.tagManager);
        assertNotNull(mappedManagerAll.mappedSystem);
        assertNotNull(extendedManager.x);
        assertNotNull(extendedManager.y);

        assertEquals(ComponentX.class, mappedSystem.x.get(entity).getClass());
        assertEquals(ComponentY.class, mappedSystem.y.get(entity).getClass());
    }

    @Test
    public void ensure_inherited_managers_injected_by_default() {
        FailingSystem failingSystem = new FailingSystem();
        FailingManager failingManager = new FailingManager();
        World world = new World(new WorldConfiguration()
                .setSystem(failingManager)
                .setSystem(failingSystem));

        assertNotNull(failingManager.x);
        assertNotNull(failingSystem.x);
    }

    @Test(expected = MundaneWireException.class)
    public void fail_on_system_not_injected() {
        World world = new World(new WorldConfiguration()
                .setSystem(new FailingNpeSystem()));
    }

    @Test(expected = MundaneWireException.class)
    public void fail_on_manager_not_injected() {
        World world = new World(new WorldConfiguration()
                .setSystem(new FailingNpeManager()));
    }

    @Test
    public void inject_pojo_object() {
        World world = new World(new WorldConfiguration()
                .setSystem(TagManager.class)
                .setSystem(new MappedSystem())
                .setSystem(new MappedSystemAll()));

        PojoWireNoWorld obj = new PojoWireNoWorld();
        world.inject(obj);

        assertNotNull(obj.componentXMapper);
        assertNotNull(obj.tagManager);
        assertNotNull(obj.mappedSystem);
    }

    @Test
    public void inject_anything_into_everything() {
        World world = new World(new WorldConfiguration()
                .register("world")
                .register("hupp", "n1")
                .register("blergh", "n2")
                .setSystem(TagManager.class));

        SomeThing st = new SomeThing();
        world.inject(st);

        assertNotNull(st.tagManager);
        assertNotNull(st.world);
        assertEquals("n1", st.helloN1);
        assertEquals("world", st.hello);
        assertEquals("n2", st.helloN2);
    }

    @Test
    public void inject_wired_world_and_notwired_world_into_everything() {
        World wiredWorld = new World(new WorldConfiguration());
        World setWorld = new World(new WorldConfiguration());
        World world = new World(new WorldConfiguration()
                .register(wiredWorld));

        WiredNotWiredWorldThing wt = new WiredNotWiredWorldThing();
        wt.worldSet = setWorld;
        world.inject(wt);

        assertNotNull(wt.worldWired);
        assertNotNull(wt.worldNotWired);
        assertNotNull(wt.worldSet);
        assertNotEquals(wt.worldWired, wt.worldNotWired);
        assertNotEquals(wt.worldWired, wt.worldSet);
        assertNotEquals(wt.worldNotWired, wt.worldSet);
    }

    @Test(expected = MundaneWireException.class)
    public void inject_wired_world_missing() {
        World world = new World(new WorldConfiguration());

        WiredNotWiredWorldThing wt = new WiredNotWiredWorldThing();
        world.inject(wt);

        assertTrue("Should fail!", true);
    }

    @Test
    public void try_inject_on_wired_object_mirrors_inject_behaviour() {
        World world = new World(new WorldConfiguration().register("world").setSystem(TagManager.class));
        SomeThing st = new SomeThing();
        world.inject(st, false);
        assertEquals("world", st.hello);
    }

    @Test
    public void try_inject_on_plain_object_does_nothing() {
        World world = new World(new WorldConfiguration());
        Object object = new Object();
        world.inject(object, false);
    }

    @Test
    public void When_explicit_inject_pojo_into_wire_injectinherited_false_annotated_pojo_Then_should_NOT_inject_into_superclass_fields() {

        class InjectedPojo {}

        class PojoSuper {
            @Wire
            public InjectedPojo testManager;
        }

        @Wire(injectInherited = false)
        class Pojo extends PojoSuper {
        }

        Pojo pojo = new Pojo();

        World w = new World(new WorldConfiguration().register(new InjectedPojo()));
        w.inject(pojo, true);

        Assert.assertNull(pojo.testManager);
    }

    @Test
    public void When_explicit_inject_pojo_into_wire_injectinherited_true_annotated_pojo_Then_should_inject_into_superclass_fields() {

        class InjectedPojo {}

        class PojoSuper {
            @Wire
            public InjectedPojo testManager;
        }

        @Wire(injectInherited = true)
        class Pojo extends PojoSuper {
        }

        Pojo pojo = new Pojo();

        World w = new World(new WorldConfiguration().register(new InjectedPojo()));
        w.inject(pojo, true);

        Assert.assertNotNull(pojo.testManager);
    }

    @Test
    public void When_explicit_inject_pojo_into_wireless_pojo_Then_should_inject_into_superclass_fields() {

        class InjectedPojo {}

        class PojoSuper {
            @Wire
            public InjectedPojo testManager;
        }

        class Pojo extends PojoSuper {
        }

        Pojo pojo = new Pojo();

        World w = new World(new WorldConfiguration().register(new InjectedPojo()));
        w.inject(pojo, true);

        Assert.assertNotNull(pojo.testManager);
    }

    @Test
    @SuppressWarnings("static-method")
    public void inject_static_field() {
        World w = new World(new WorldConfiguration()
                .setSystem(new ManagerWithStaticField()));
        w.process();

        assertNotNull(ManagerWithStaticField.mapper);
    }

    @Test
    @SuppressWarnings("static-method")
    public void inject_static_field_extended() {
        World w = new World(new WorldConfiguration()
                .setSystem(new ExtendedStaticManager()));
        w.process();

        assertNotNull(ManagerWithStaticField.mapper);
    }

    @Test
    @SuppressWarnings("static-method")
    public void inject_static_field_inherited() {
        World w = new World(new WorldConfiguration()
                .setSystem(new ManagerWithStaticField()));
        w.process();

        assertNotNull(ManagerWithStaticField.mapper);
    }

    private static class SomeThing {
        @Wire(name = "hupp", failOnNull = false)
        private String helloN1;
        @Wire
        private String hello;
        @Wire(name = "blergh", failOnNull = false)
        private String helloN2;
        World world;

        private TagManager tagManager;
    }

    private static class PojoWireNoWorld {
        private ComponentMapper<ComponentX> componentXMapper;
        private TagManager tagManager;
        private MappedSystem mappedSystem;
    }

    private static class WiredNotWiredWorldThing {
        @Wire
        World worldWired;
        World worldNotWired;
        @SkipWire
        World worldSet;
    }

    private static class MappedSystemAll extends EntityProcessingSystem {
        private ComponentMapper<ComponentX> x;
        private ComponentMapper<ComponentY> y;
        private TagManager tagManager;
        private MappedSystem mappedSystem;

        @SuppressWarnings("unchecked")
        public MappedSystemAll() {
            super(Aspect.all(ComponentX.class, ComponentY.class));
        }

        @Override
        protected void process(Entity e) {
        }
    }

    private static class MappedSystem extends EntityProcessingSystem {
        @Wire
        private ComponentMapper<ComponentX> x;
        @Wire
        private ComponentMapper<ComponentY> y;
        @Wire
        private TagManager tagManager;
        @Wire
        private MappedSystemAll mappedSystemAll;

        @SuppressWarnings("unchecked")
        public MappedSystem() {
            super(Aspect.all(ComponentX.class, ComponentY.class));
        }

        @Override
        protected void process(Entity e) {
        }
    }

    private static class ExtendedStaticManager extends ManagerWithStaticField {
    }

    private static class ManagerWithStaticField extends Manager {
        static ComponentMapper<ComponentX> mapper;
    }

    private static class MappedManager extends Manager {
        @Wire
        private ComponentMapper<ComponentX> x;
        @Wire
        private ComponentMapper<ComponentY> y;
        @Wire
        private MappedSystem mappedSystem;
        @Wire
        private TagManager tagManager;
    }

    private static class MappedManagerAll extends Manager {
        private ComponentMapper<ComponentX> x;
        private ComponentMapper<ComponentY> y;
        private MappedSystem mappedSystem;
        private TagManager tagManager;
    }

    private static class BaseManager extends Manager {
        protected ComponentMapper<ComponentX> x;
    }

    private static class ExtendedManager extends BaseManager {
        private ComponentMapper<ComponentY> y;
    }

    private static class FailingManager extends BaseManager {
        @SuppressWarnings("unused")
        private ComponentMapper<ComponentY> y;
    }

    private static abstract class BaseSystem extends VoidEntitySystem {
        protected ComponentMapper<ComponentX> x;
    }

    private static class ExtendedSystem extends BaseSystem {
        private ComponentMapper<ComponentY> y;

        @Override
        protected void processSystem() {
        }
    }

    private static class FailingSystem extends BaseSystem {
        @SuppressWarnings("unused")
        private FailingManager manager;

        @Override
        protected void processSystem() {
        }
    }

    private static class FailingNpeSystem extends VoidEntitySystem {
        @SuppressWarnings("unused")
        private FailingManager manager;

        @Override
        protected void processSystem() {
        }
    }

    private static class FailingNpeManager extends Manager {
        @SuppressWarnings("unused")
        private FailingSystem fail;
    }

    private static class CustomInjectedManager extends Manager {
        @Wire
        InjectMe injectMe;
        InjectMe nullInjectMe;
    }

    private static class CustomNamedInjectedManager extends Manager {
        @Wire(name = "hi")
        InjectMe injectMe;
        InjectMe nullInjectMe;
    }

    public static class InjectMe {
    }
}
