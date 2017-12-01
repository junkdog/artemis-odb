package com.artemis;

import com.artemis.component.*;
import com.artemis.system.AspectDescriptorSystem;
import com.google.gwt.junit.client.GWTTestCase;

import static com.artemis.Aspect.all;

public class AspectFieldHandlerTest extends GWTTestCase {
	private Aspect.Builder reference = all(ComponentX.class, ComponentY.class)
		.one(ReusedComponent.class, EntityHolder.class)
		.exclude(PooledString.class);

	@Override
	public String getModuleName() {
		return "com.ArtemisTest";
	}

	public void test_inject_aspect_fields_pojo() throws Exception {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new AspectDescriptorSystem())
			.register(new Object());
		EntityWorld world = new EntityWorld(worldConfiguration);

		AspectDescriptorPojo withAspectFields = new AspectDescriptorPojo();
		world.inject(withAspectFields);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);

		checkArchetype(world, withAspectFields.archetype);
	}


	public void test_inject_aspect_fields_system() throws Exception {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new AspectDescriptorSystem())
			.register(new Object());
		EntityWorld world = new EntityWorld(worldConfiguration);

		AspectDescriptorSystem withAspectFields = world.getSystem(AspectDescriptorSystem.class);

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
}
