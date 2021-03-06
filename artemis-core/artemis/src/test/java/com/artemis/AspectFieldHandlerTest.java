package com.artemis;

import com.artemis.annotations.All;
import com.artemis.annotations.AspectDescriptor;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.One;
import com.artemis.component.*;
import org.junit.Test;

import static com.artemis.Aspect.all;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AspectFieldHandlerTest {
	private Aspect.Builder reference = all(ComponentX.class, ComponentY.class)
		.one(ReusedComponent.class, EntityHolder.class)
		.exclude(PooledString.class);

	@Test
	public void inject_aspect_fields_pojo() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.register(new Object());
		World world = new World(worldConfiguration);

		ObjectAspectFields withAspectFields = new ObjectAspectFields();
		world.inject(withAspectFields);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
        
        assertEquals(reference, withAspectFields.abAllOneExclude);
        assertNotNull(withAspectFields.aspectAllOneExclude);
        assertEquals(reference, withAspectFields.subAllOneExclude.getAspectBuilder());
        assertNotNull(withAspectFields.transmuterAllOneExclude);
        
		checkArchetype(world, withAspectFields.archetypeAll);
	}

	@Test
	public void inject_aspect_fields_system() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
				.setSystem(new SomeSystem())
				.register(new Object());
		World world = new World(worldConfiguration);

		SomeSystem withAspectFields = world.getSystem(SomeSystem.class);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
	}


	@Test
	public void no_exception_When_injecting_system_with_unannotated_fields() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
				.setSystem(new NullInjectionSystem())
				.register(new Object());
		World world = new World(worldConfiguration);

		NullInjectionSystem withAspectFields = world.getSystem(NullInjectionSystem.class);
		assertNull(withAspectFields.archetypeNotInjected);
		assertNull(withAspectFields.subAllOneExcludeNotInjected);
		assertNull(withAspectFields.transmuterAllOneExcludeNotInjected);
		assertNull(withAspectFields.aspectAllOneExcludeNotInjected);
		assertNull(withAspectFields.abAllOneExcludeNotInjected);
		assertNull(withAspectFields.archetypeAllNotInjected);
	}

	@Test(expected = RuntimeException.class)
	public void runtime_exception_When_all_annotation_value_is_empty_on_archetype() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
				.setSystem(new InvalidAllUseSystem())
				.register(new Object());
		World world = new World(worldConfiguration);
		world.getSystem(InvalidAllUseSystem.class);
	}

	private static void checkArchetype(World world, Archetype archetype) {
		Entity e = world.getEntity(world.create(archetype));
		assertNotNull(e.getComponent(ComponentX.class));
		assertNotNull(e.getComponent(ReusedComponent.class));
		assertNull(e.getComponent(ComponentY.class));
	}

	public static class NullInjectionSystem extends BaseSystem {
		public Archetype archetypeNotInjected;
		public EntitySubscription subAllOneExcludeNotInjected;
		public EntityTransmuter transmuterAllOneExcludeNotInjected;
		public Aspect aspectAllOneExcludeNotInjected;
		public Aspect.Builder abAllOneExcludeNotInjected;
		public Archetype archetypeAllNotInjected;

		@Override protected void processSystem() { }
	}


	public static class InvalidAllUseSystem extends BaseSystem {
		@All()
		public Archetype archetypeNotInjected;
		@Override protected void processSystem() { }
	}

	private static class ObjectAspectFields {
		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public EntitySubscription sub;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public EntityTransmuter transmuter;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public Aspect aspect;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public Aspect.Builder ab;

		@AspectDescriptor(
			all = {ComponentX.class, ReusedComponent.class})
		public Archetype archetype;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntitySubscription subAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntityTransmuter transmuterAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect aspectAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect.Builder abAllOneExclude;

		@All({ ComponentX.class, ReusedComponent.class })
		public Archetype archetypeAll;
	}

	private static class SomeSystem extends BaseSystem {
		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public EntitySubscription sub;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public EntityTransmuter transmuter;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public Aspect aspect;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class})
		public Aspect.Builder ab;

		@AspectDescriptor(
			all = {ComponentX.class, ReusedComponent.class})
		public Archetype archetype;
		
		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntitySubscription subAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntityTransmuter transmuterAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect aspectAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect.Builder abAllOneExclude;

		@All({ ComponentX.class, ReusedComponent.class })
		public Archetype archetypeAll;

		@Override
		protected void processSystem() {}
	}
	
}
