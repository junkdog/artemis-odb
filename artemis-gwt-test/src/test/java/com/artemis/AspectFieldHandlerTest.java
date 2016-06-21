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
		World world = new World(worldConfiguration);

		AspectDescriptorPojo withAspectFields = new AspectDescriptorPojo();
		world.inject(withAspectFields);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);
	}


	public void test_inject_aspect_fields_system() throws Exception {
		WorldConfiguration worldConfiguration = new WorldConfiguration()
			.setSystem(new AspectDescriptorSystem())
			.register(new Object());
		World world = new World(worldConfiguration);

		AspectDescriptorSystem withAspectFields = world.getSystem(AspectDescriptorSystem.class);

		assertEquals(reference, withAspectFields.ab);
		assertNotNull(withAspectFields.aspect);
		assertEquals(reference, withAspectFields.sub.getAspectBuilder());
		assertNotNull(withAspectFields.transmuter);
	}

}
