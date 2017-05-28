package com.artemis;

import com.artemis.annotations.AspectDescriptor;
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
	public void inject_aspect_fields_pojo() throws Exception {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.register(new Object());
		EntityWorld world = new EntityWorld(worldConfiguration);

		ObjectAspectFields withAspectFields = new ObjectAspectFields();
		world.inject(withAspectFields);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
	}

	@Test
	public void inject_aspect_fields_system() throws Exception {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.register(new Object());
		EntityWorld world = new EntityWorld(worldConfiguration);

		SomeSystem withAspectFields = world.getSystem(SomeSystem.class);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
	}

	private static void checkArchetype(EntityWorld world, Archetype archetype) {
		Entity e = world.getEntity(world.create(archetype));
		assertNotNull(e.getComponent(ComponentX.class));
		assertNotNull(e.getComponent(ReusedComponent.class));
		assertNull(e.getComponent(ComponentY.class));
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

		@Override
		protected void processSystem() {}
	}
}
