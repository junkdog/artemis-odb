package com.artemis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.ComponentZ;

public class AspectTest {

	private ComponentManager cm;
	private AspectSubscriptionManager asm;
	
	private ComponentMapper<ComponentX> xM;
	private ComponentMapper<ComponentY> yM;
	private ComponentMapper<ComponentZ> zM;

	@Test
	public void testNoDefaultAspect() {
		world(null);
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect defaultAspect = asm.get(Aspect.all()).getAspect();
		assertFalse(defaultAspect.allSet.unsafeGet(indexX));
		assertFalse(defaultAspect.allSet.unsafeGet(indexY));
		assertFalse(defaultAspect.allSet.unsafeGet(indexZ));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexX));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexY));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexZ));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexX));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexY));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexZ));

		Aspect aspect = asm.get(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class)).getAspect();
		assertTrue(aspect.allSet.unsafeGet(indexX));
		assertFalse(aspect.allSet.unsafeGet(indexY));
		assertFalse(aspect.allSet.unsafeGet(indexZ));
		assertFalse(aspect.oneSet.unsafeGet(indexX));
		assertTrue(aspect.oneSet.unsafeGet(indexY));
		assertFalse(aspect.oneSet.unsafeGet(indexZ));
		assertFalse(aspect.exclusionSet.unsafeGet(indexX));
		assertFalse(aspect.exclusionSet.unsafeGet(indexY));
		assertTrue(aspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspect() {
		world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect defaultAspect = asm.get(Aspect.all()).getAspect();
		assertTrue(defaultAspect.allSet.unsafeGet(indexX));
		assertFalse(defaultAspect.allSet.unsafeGet(indexY));
		assertFalse(defaultAspect.allSet.unsafeGet(indexZ));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexX));
		assertTrue(defaultAspect.oneSet.unsafeGet(indexY));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexZ));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexX));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexY));
		assertTrue(defaultAspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspectDisabled() {
		world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect defaultAspect = asm.get(Aspect.defaults(false)).getAspect();
		assertFalse(defaultAspect.allSet.unsafeGet(indexX));
		assertFalse(defaultAspect.allSet.unsafeGet(indexY));
		assertFalse(defaultAspect.allSet.unsafeGet(indexZ));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexX));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexY));
		assertFalse(defaultAspect.oneSet.unsafeGet(indexZ));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexX));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexY));
		assertFalse(defaultAspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspect_ComponentsSkippedIfUsed() {
		world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect aspect = asm.get(Aspect.all(ComponentY.class).one(ComponentZ.class).exclude(ComponentX.class)).getAspect();
		assertFalse(aspect.allSet.unsafeGet(indexX));
		assertTrue(aspect.allSet.unsafeGet(indexY));
		assertFalse(aspect.allSet.unsafeGet(indexZ));
		assertFalse(aspect.oneSet.unsafeGet(indexX));
		assertFalse(aspect.oneSet.unsafeGet(indexY));
		assertTrue(aspect.oneSet.unsafeGet(indexZ));
		assertTrue(aspect.exclusionSet.unsafeGet(indexX));
		assertFalse(aspect.exclusionSet.unsafeGet(indexY));
		assertFalse(aspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspect_ComponentsSkippedIfUsed_All() {
		world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect aspect = asm.get(Aspect.all(ComponentY.class, ComponentZ.class)).getAspect();
		assertTrue(aspect.allSet.unsafeGet(indexX));
		assertTrue(aspect.allSet.unsafeGet(indexY));
		assertTrue(aspect.allSet.unsafeGet(indexZ));
		assertFalse(aspect.oneSet.unsafeGet(indexX));
		assertFalse(aspect.oneSet.unsafeGet(indexY));
		assertFalse(aspect.oneSet.unsafeGet(indexZ));
		assertFalse(aspect.exclusionSet.unsafeGet(indexX));
		assertFalse(aspect.exclusionSet.unsafeGet(indexY));
		assertFalse(aspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspect_ComponentsSkippedIfUsed_One() {
		world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect aspect = asm.get(Aspect.one(ComponentX.class, ComponentZ.class)).getAspect();
		assertFalse(aspect.allSet.unsafeGet(indexX));
		assertFalse(aspect.allSet.unsafeGet(indexY));
		assertFalse(aspect.allSet.unsafeGet(indexZ));
		assertTrue(aspect.oneSet.unsafeGet(indexX));
		assertTrue(aspect.oneSet.unsafeGet(indexY));
		assertTrue(aspect.oneSet.unsafeGet(indexZ));
		assertFalse(aspect.exclusionSet.unsafeGet(indexX));
		assertFalse(aspect.exclusionSet.unsafeGet(indexY));
		assertFalse(aspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspect_ComponentsSkippedIfUsed_Exclude() {
		world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		ComponentTypeFactory ctf = cm.getTypeFactory();
		int indexX = ctf.getIndexFor(ComponentX.class);
		int indexY = ctf.getIndexFor(ComponentY.class);
		int indexZ = ctf.getIndexFor(ComponentZ.class);

		Aspect aspect = asm.get(Aspect.exclude(ComponentX.class, ComponentY.class)).getAspect();
		assertFalse(aspect.allSet.unsafeGet(indexX));
		assertFalse(aspect.allSet.unsafeGet(indexY));
		assertFalse(aspect.allSet.unsafeGet(indexZ));
		assertFalse(aspect.oneSet.unsafeGet(indexX));
		assertFalse(aspect.oneSet.unsafeGet(indexY));
		assertFalse(aspect.oneSet.unsafeGet(indexZ));
		assertTrue(aspect.exclusionSet.unsafeGet(indexX));
		assertTrue(aspect.exclusionSet.unsafeGet(indexY));
		assertTrue(aspect.exclusionSet.unsafeGet(indexZ));
	}

	@Test
	public void testDefaultAspectEntityTest() {
		World world = world(Aspect.all(ComponentX.class).one(ComponentY.class).exclude(ComponentZ.class));
		Entity entity = world.createEntity();

		EntitySubscription subscription = world.getAspectSubscriptionManager().get(Aspect.all(ComponentY.class));
		Aspect aspect = subscription.getAspect();

		world.process();
		assertEquals(false, aspect.isInterested(entity));

		world.process();
		xM.create(entity);
		assertEquals(false, aspect.isInterested(entity));

		world.process();
		yM.create(entity);
		assertEquals(true, aspect.isInterested(entity));

		world.process();
		zM.create(entity);
		assertEquals(false, aspect.isInterested(entity));
	}

	private World world(Aspect.Builder aspect) {
		World world = new World(new WorldConfigurationBuilder()
				.defaultAspect(aspect)
				.build());
		
		world.inject(this);
		return world;
	}

}
