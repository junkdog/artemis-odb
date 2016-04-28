package com.artemis.link;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import org.junit.Test;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE;
import static com.artemis.annotations.LinkPolicy.Policy.SKIP;
import static org.junit.Assert.*;

public class EntityLinkManagerTest {

	@Test
	public void uni_link_explicit_field_int_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<EntityLink> mapper = world.getMapper(EntityLink.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(EntityLink.class, "otherId", new MyLinkListener(e, otherA, otherB));


		// establish link
		world.process();
		mapper.create(e).otherId = otherA;
		world.process();

		// target change
		mapper.get(e).otherId = otherB;
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertEquals(-1, mapper.get(e).otherId);

		// on restablish
		mapper.get(e).otherId = otherA;
		world.process();
		assertEquals(otherA, mapper.get(e).otherId);

		// kill link
		world.delete(e);
		world.process();
	}


	@Test
	public void uni_link_automatic_field_entity_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<EntityLinkB> mapper = world.getMapper(EntityLinkB.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(EntityLinkB.class, new MyLinkListener(e, otherA, otherB));

		// establish link
		world.process();
		mapper.create(e).other = world.getEntity(otherA);
		world.process();

		// target change
		mapper.get(e).other = world.getEntity(otherB);
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertNull(mapper.get(e).other);

		// on restablish
		mapper.get(e).other = world.getEntity(otherA);
		world.process();
		assertEquals(otherA, mapper.get(e).other.getId());

		// kill link
		world.delete(e);
		world.process();
	}

	@Test
	public void uni_link_automatic_field_entity_check_source_policy_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<EntityLinkC> mapper = world.getMapper(EntityLinkC.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(EntityLinkC.class, new LinkListener() {
			@Override
			public void onLinkEstablished(int sourceId, int targetId) {
				assertEquals(sourceId, e);
				assertEquals(targetId, otherA);
			}

			@Override
			public void onLinkKilled(int sourceId, int target) {
				assertEquals(sourceId, e);
				assertEquals(target, otherA);
			}

			@Override
			public void onTargetDead(int sourceId, int deadTargetId) {
				fail();
			}

			@Override
			public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
				fail();
			}
		});

		// establish link
		world.process();
		mapper.create(e).other = world.getEntity(otherA);
		world.process();

		// target change
		mapper.get(e).other = world.getEntity(otherB);
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertNotNull(mapper.get(e).other); // because only checking source

		// on restablish
		mapper.get(e).other = world.getEntity(otherA);
		world.process();
		assertEquals(otherA, mapper.get(e).other.getId());

		// kill link
		world.delete(e);
		world.process();
	}

	@Test
	public void uni_link_automatic_field_entity_skip_policy_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<EntityLinkSkip> mapper = world.getMapper(EntityLinkSkip.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(EntityLinkSkip.class, new LinkListener() {
			@Override
			public void onLinkEstablished(int sourceId, int targetId) {
				fail();
			}

			@Override
			public void onLinkKilled(int sourceId, int target) {
				fail();
			}

			@Override
			public void onTargetDead(int sourceId, int deadTargetId) {
				fail();
			}

			@Override
			public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
				fail();
			}
		});

		// establish link
		world.process();
		mapper.create(e).other = world.getEntity(otherA);
		world.process();

		// target change
		mapper.get(e).other = world.getEntity(otherB);
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertNotNull(mapper.get(e).other); // because skipping everything

		// on restablish
		mapper.get(e).other = world.getEntity(otherA);
		world.process();
		assertEquals(otherA, mapper.get(e).other.getId());

		// kill link
		world.delete(e);
		world.process();
	}

	public static class EntityLink extends Component {
		@EntityId
		public int otherId;
		public int nothingHere;
	}

	public static class EntityLinkB extends Component {
		public Entity other;
		public int nothingHere;
	}

	public static class EntityLinkC extends Component {
		@LinkPolicy(CHECK_SOURCE)
		public Entity other;
	}

	public static class EntityLinkSkip extends Component {
		@LinkPolicy(SKIP)
		public Entity other;
	}

	private static class MyLinkListener implements LinkListener {
		private final int e;
		private final int otherA;
		private final int otherB;

		public MyLinkListener(int e, int otherA, int otherB) {
			this.e = e;
			this.otherA = otherA;
			this.otherB = otherB;
		}

		@Override
		public void onLinkEstablished(int sourceId, int targetId) {
			assertEquals(sourceId, e);
			assertEquals(targetId, otherA);
		}

		@Override
		public void onLinkKilled(int sourceId, int target) {
			assertEquals(sourceId, e);
			assertEquals(target, otherA);
		}

		@Override
		public void onTargetDead(int sourceId, int deadTargetId) {
			assertEquals(sourceId, e);
			assertEquals(deadTargetId, otherB);
		}

		@Override
		public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
			assertEquals(sourceId, e);
			assertEquals(targetId, otherB);
			assertEquals(oldTargetId, otherA);
		}
	}
}