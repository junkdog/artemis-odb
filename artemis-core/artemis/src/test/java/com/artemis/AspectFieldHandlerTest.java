package com.artemis;

import com.artemis.annotations.All;
import com.artemis.annotations.AspectDescriptor;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.One;
import com.artemis.component.*;
import org.junit.Test;

import static com.artemis.Aspect.all;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AspectFieldHandlerTest {
	private Aspect.Builder reference = all(ComponentX.class, ComponentY.class)
			.one(ReusedComponent.class, EntityHolder.class)
			.exclude(PooledString.class)
			.defaults(false);
	
	private Aspect.Builder referenceDefaults = all(ComponentX.class, ComponentY.class, ComponentZ.class)
			.one(ReusedComponent.class, EntityHolder.class)
			.exclude(PooledString.class)
			.defaults(false);

	@Test
	public void inject_aspect_fields_pojo() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.register(new Object());
		World world = new World(worldConfiguration);

		ObjectAspectFields withAspectFields = new ObjectAspectFields();
		world.inject(withAspectFields);

		assertEquals(reference, withAspectFields.ab);
		assertFalse(withAspectFields.ab.defaults);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertFalse(withAspectFields.sub.getAspectBuilder().defaults);
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
        
        assertEquals(reference, withAspectFields.abAllOneExclude);
		assertFalse(withAspectFields.abAllOneExclude.defaults);
        assertNotNull(withAspectFields.aspectAllOneExclude);
        assertEquals(reference, withAspectFields.subAllOneExclude.getAspectBuilder());
		assertFalse(withAspectFields.subAllOneExclude.getAspectBuilder().defaults);
        assertNotNull(withAspectFields.transmuterAllOneExclude);
        
		checkArchetype(world, withAspectFields.archetypeAll);
	}

	@Test
	public void inject_aspect_fields_pojo_defaults() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.setDefaultAspect(Aspect.all(ComponentZ.class))
			.register(new Object());
		World world = new World(worldConfiguration);

		// no defaults
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

		// defaults
		ObjectAspectFieldsDefaults withAspectFieldsDefaults = new ObjectAspectFieldsDefaults();
		world.inject(withAspectFieldsDefaults);

		assertEquals(reference, withAspectFieldsDefaults.abDefaults);
		assertTrue(withAspectFieldsDefaults.abDefaults.defaults);
		assertNotNull(withAspectFieldsDefaults.aspectDefaults);
		assertEquals(referenceDefaults, withAspectFieldsDefaults.subDefaults.getAspectBuilder());
		assertTrue(withAspectFieldsDefaults.subDefaults.getAspectBuilder().defaults);
		assertNotNull(withAspectFieldsDefaults.transmuterDefaults);
		
		checkArchetypeDefaults(world, withAspectFieldsDefaults.archetypeDefaults);
		
		assertEquals(reference, withAspectFieldsDefaults.abAllOneExcludeDefaults);
		assertTrue(withAspectFieldsDefaults.abAllOneExcludeDefaults.defaults);
		assertNotNull(withAspectFieldsDefaults.aspectAllOneExcludeDefaults);
		assertEquals(referenceDefaults, withAspectFieldsDefaults.subAllOneExcludeDefaults.getAspectBuilder());
		assertTrue(withAspectFieldsDefaults.subAllOneExcludeDefaults.getAspectBuilder().defaults);
		assertNotNull(withAspectFieldsDefaults.transmuterAllOneExcludeDefaults);
		
		checkArchetypeDefaults(world, withAspectFieldsDefaults.archetypeAllDefaults);
	}

	@Test
	public void inject_aspect_fields_system() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.register(new Object());
		World world = new World(worldConfiguration);

		SomeSystem withAspectFields = world.getSystem(SomeSystem.class);

		assertEquals(reference, withAspectFields.ab);
		assertFalse(withAspectFields.ab.defaults);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertFalse(withAspectFields.sub.getAspectBuilder().defaults);
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
	}
	
	@Test
	public void inject_aspect_fields_system_defaults() {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
				.setSystem(new SomeSystem())
				.setSystem(new SomeSystemDefaults())
				.setDefaultAspect(Aspect.all(ComponentZ.class))
				.register(new Object());
		World world = new World(worldConfiguration);
		
		// no defaults
		SomeSystem withAspectFields = world.getSystem(SomeSystem.class);

		assertEquals(reference, withAspectFields.ab);
		assertFalse(withAspectFields.ab.defaults);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertFalse(withAspectFields.sub.getAspectBuilder().defaults);
		assertNotNull(withAspectFields.transmuter);
		
		checkArchetype(world, withAspectFields.archetype);
		
		// defaults
		SomeSystemDefaults withAspectFieldsDefaults = world.getSystem(SomeSystemDefaults.class);

		assertEquals(reference, withAspectFieldsDefaults.abDefaults);
		assertTrue(withAspectFieldsDefaults.abDefaults.defaults);
		assertNotNull(withAspectFieldsDefaults.aspectDefaults);
		assertEquals(referenceDefaults, withAspectFieldsDefaults.subDefaults.getAspectBuilder());
		assertTrue(withAspectFieldsDefaults.subDefaults.getAspectBuilder().defaults);
		assertNotNull(withAspectFieldsDefaults.transmuterDefaults);
		
		checkArchetypeDefaults(world, withAspectFieldsDefaults.archetypeDefaults);
	}
	
	private static void checkArchetype(World world, Archetype archetype) {
		Entity e = world.getEntity(world.create(archetype));
		assertNotNull(e.getComponent(ComponentX.class));
		assertNotNull(e.getComponent(ReusedComponent.class));
		assertNull(e.getComponent(ComponentY.class));
	}
	
	private static void checkArchetypeDefaults(World world, Archetype archetype) {
		Entity e = world.getEntity(world.create(archetype));
		assertNotNull(e.getComponent(ComponentX.class));
		assertNotNull(e.getComponent(ReusedComponent.class));
		assertNull(e.getComponent(ComponentY.class));
		assertNull(e.getComponent(ComponentZ.class));
	}

	private static class ObjectAspectFields {
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
		public EntitySubscription sub;
		
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
		public EntityTransmuter transmuter;
		
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
		public Aspect aspect;
		
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
		public Aspect.Builder ab;

		@AspectDescriptor(
				all = {ComponentX.class, ReusedComponent.class},
				defaults = false)
		public Archetype archetype;
		
		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntitySubscription subAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntityTransmuter transmuterAllOneExclude;
		
		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect aspectAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect.Builder abAllOneExclude;
		
		@All({ ComponentX.class, ReusedComponent.class })
		@Exclude(excludeDefaults = true)
		public Archetype archetypeAll;
	}

	private static class ObjectAspectFieldsDefaults {
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class})
		public EntitySubscription subDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class})
		public EntityTransmuter transmuterDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class})
		public Aspect aspectDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class})
		public Aspect.Builder abDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ReusedComponent.class})
		public Archetype archetypeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntitySubscription subAllOneExcludeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntityTransmuter transmuterAllOneExcludeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect aspectAllOneExcludeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(PooledString.class)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect.Builder abAllOneExcludeDefaults;

		@All({ ComponentX.class, ReusedComponent.class })
		public Archetype archetypeAllDefaults;
	}

	private static class SomeSystem extends BaseSystem {
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
			public EntitySubscription sub;

		@AspectDescriptor(
			all = {ComponentX.class, ComponentY.class},
			exclude = PooledString.class,
			one = {ReusedComponent.class, EntityHolder.class},
			defaults = false)
		public EntityTransmuter transmuter;
		
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
			public Aspect aspect;
		
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = false)
			public Aspect.Builder ab;
		
		@AspectDescriptor(
				all = {ComponentX.class, ReusedComponent.class},
				defaults = false)
			public Archetype archetype;
		
		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntitySubscription subAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntityTransmuter transmuterAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect aspectAllOneExclude;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = true)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect.Builder abAllOneExclude;

		@All({ ComponentX.class, ReusedComponent.class })
		@Exclude(excludeDefaults = true)
		public Archetype archetypeAll;

		@Override
		protected void processSystem() {}
	}
	
	private static class SomeSystemDefaults extends BaseSystem {
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = true)
		public EntitySubscription subDefaults;
		
		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = true)
		public EntityTransmuter transmuterDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = true)
		public Aspect aspectDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ComponentY.class},
				exclude = PooledString.class,
				one = {ReusedComponent.class, EntityHolder.class},
				defaults = true)
		public Aspect.Builder abDefaults;

		@AspectDescriptor(
				all = {ComponentX.class, ReusedComponent.class},
				defaults = true)
		public Archetype archetypeDefaults;
		
		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = false)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntitySubscription subAllOneExcludeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = false)
		@One({ ReusedComponent.class, EntityHolder.class })
		public EntityTransmuter transmuterAllOneExcludeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = false)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect aspectAllOneExcludeDefaults;

		@All({ ComponentX.class, ComponentY.class })
		@Exclude(value = PooledString.class, excludeDefaults = false)
		@One({ ReusedComponent.class, EntityHolder.class })
		public Aspect.Builder abAllOneExcludeDefaults;

		@All({ ComponentX.class, ReusedComponent.class })
		@Exclude(excludeDefaults = false)
		public Archetype archetypeAllDefaults;

		@Override
		protected void processSystem() {}
	}
	
}
