package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.artemis.annotations.Wire;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.VoidEntitySystem;

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
				.setManager(TagManager.class)
				.setManager(mappedManager)
				.setManager(mappedManagerAll)
				.setManager(extendedManager)
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
	
	@Test(expected=MundaneWireException.class)
	public void ensure_inherited_systems_not_injected() {
		World world = new World(new WorldConfiguration()
				.setSystem(new FailingSystem()));
	}
	
	@Test
	public void ensure_inherited_managers_not_injected() {
		FailingSystem failingSystem = new FailingSystem();
		FailingManager failingManager = new FailingManager();
		World world = new World(new WorldConfiguration()
				.setManager(failingManager)
				.setSystem(failingSystem));

		assertNull(failingManager.x);
		assertNull(failingSystem.x);
	}
	
	@Test(expected=MundaneWireException.class)
	public void fail_on_system_not_injected() {
		World world = new World(new WorldConfiguration()
				.setSystem(new FailingNpeSystem()));
	}
	
	@Test(expected=MundaneWireException.class)
	public void fail_on_manager_not_injected() {
		World world = new World(new WorldConfiguration()
				.setManager(new FailingNpeManager()));
	}
	
	@Test
	public void inject_pojo_object() {
		World world = new World(new WorldConfiguration()
				.setManager(TagManager.class)
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
			.setManager(TagManager.class));

		SomeThing st = new SomeThing();
		world.inject(st);

		assertNotNull(st.tagManager);
		assertEquals("n1", st.helloN1);
		assertEquals("world", st.hello);
		assertEquals("n2", st.helloN2);
	}

	@Test
	public void try_inject_on_wired_object_mirrors_inject_behaviour() {
		World world = new World(new WorldConfiguration().register("world").setManager(TagManager.class));
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
	
	@Test @SuppressWarnings("static-method")
	public void inject_static_field() {
		World w = new World(new WorldConfiguration()
				.setManager(new ManagerWithStaticField()));
		w.process();
		
		assertNotNull(ManagerWithStaticField.mapper);
	}
	
	@Test @SuppressWarnings("static-method")
	public void inject_static_field_extended() {
		World w = new World(new WorldConfiguration()
				.setManager(new ExtendedStaticManager()));
		w.process();
		
		assertNotNull(ManagerWithStaticField.mapper);
	}
	
	@Test @SuppressWarnings("static-method")
	public void inject_static_field_inherited() {
		World w = new World(new WorldConfiguration()
				.setManager(new ManagerWithStaticField()));
		w.process();
		
		assertNotNull(ManagerWithStaticField.mapper);
	}

	@Wire
	private static class SomeThing {
		@Wire(name="hupp") private String helloN1; 
		@Wire private String hello;
		@Wire(name="blergh") private String helloN2; 
		
		private TagManager tagManager;
	}
	
	@Wire
	private static class PojoWireNoWorld {
		private ComponentMapper<ComponentX> componentXMapper;
		private TagManager tagManager;
		private MappedSystem mappedSystem;
	}
	
	@Wire
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
		protected void process(Entity e) {}
	}
	
	private static class MappedSystem extends EntityProcessingSystem {
		@Wire private ComponentMapper<ComponentX> x;
		@Wire private ComponentMapper<ComponentY> y;
		@Wire private TagManager tagManager;
		@Wire private MappedSystemAll mappedSystemAll;
		
		@SuppressWarnings("unchecked")
		public MappedSystem() {
			super(Aspect.all(ComponentX.class, ComponentY.class));
		}

		@Override
		protected void process(Entity e) {}
	}
	
	@Wire(injectInherited=true)
	private static class ExtendedStaticManager extends ManagerWithStaticField {}
	
	@Wire
	private static class ManagerWithStaticField extends Manager{
		static ComponentMapper<ComponentX> mapper;
	}
	
	private static class MappedManager extends Manager {
		@Wire private ComponentMapper<ComponentX> x;
		@Wire private ComponentMapper<ComponentY> y;
		@Wire private MappedSystem mappedSystem;
		@Wire private TagManager tagManager;
	}
	
	@Wire
	private static class MappedManagerAll extends Manager {
		private ComponentMapper<ComponentX> x;
		private ComponentMapper<ComponentY> y;
		private MappedSystem mappedSystem;
		private TagManager tagManager;
	}
	
	private static class BaseManager extends Manager {
		protected ComponentMapper<ComponentX> x;
	}
	
	@Wire(injectInherited=true)
	private static class ExtendedManager extends BaseManager {
		private ComponentMapper<ComponentY> y;
	}
	
	@Wire
	private static class FailingManager extends BaseManager {
		@SuppressWarnings("unused")
		private ComponentMapper<ComponentY> y;
	}
	
	private static abstract class BaseSystem extends VoidEntitySystem {
		protected ComponentMapper<ComponentX> x;
	}
	
	@Wire(injectInherited=true)
	private static class ExtendedSystem extends BaseSystem {
		private ComponentMapper<ComponentY> y;
		
		@Override
		protected void processSystem() {}
	}
	
	@Wire
	private static class FailingSystem extends BaseSystem {
		@SuppressWarnings("unused")
		private FailingManager manager;
		
		@Override
		protected void processSystem() {}
	}
	
	@Wire
	private static class FailingNpeSystem extends VoidEntitySystem {
		@SuppressWarnings("unused")
		private FailingManager manager;
		
		@Override
		protected void processSystem() {}
	}
	
	@Wire
	private static class FailingNpeManager extends Manager {
		@SuppressWarnings("unused")
		private FailingSystem fail;
	}
}
