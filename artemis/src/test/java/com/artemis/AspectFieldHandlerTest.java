package com.artemis;

import com.artemis.annotations.AspectDescriptor;
import com.artemis.component.*;
import com.artemis.injection.*;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;
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
		World world = new World(worldConfiguration);

		ObjectAspectFields withAspectFields = new ObjectAspectFields();
		world.inject(withAspectFields);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);
	}


	@Test
	public void inject_aspect_fields_system() throws Exception {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new SomeSystem())
			.register(new Object());
		World world = new World(worldConfiguration);

		SomeSystem withAspectFields = world.getSystem(SomeSystem.class);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);
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

		@Override
		protected void processSystem() {}
	}
}
