package com.artemis.link;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import org.junit.Test;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE;
import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS;
import static com.artemis.annotations.LinkPolicy.Policy.SKIP;
import static org.junit.Assert.*;

public class EntityLinkManagerTest {

	@Test
	public void unilink_explicit_field_int_test() {
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
	public void unilink_automatic_field_entity_test() {
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
	public void unilink_automatic_field_entity_check_source_policy_test() {
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
			public void onLinkKilled(int sourceId, int targetId) {
				assertEquals(sourceId, e);
				assertEquals(targetId, otherA);
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
	public void unilink_automatic_field_entity_skip_policy_test() {
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
			public void onLinkKilled(int sourceId, int targetId) {
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

	@Test
	public void multilink_automatic_field_bag_skip_all_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<MultiLinkSkip> mapper = world.getMapper(MultiLinkSkip.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(MultiLinkSkip.class, new LinkListener() {
			@Override
			public void onLinkEstablished(int sourceId, int targetId) {
				fail();
			}

			@Override
			public void onLinkKilled(int sourceId, int targetId) {
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
		mapper.create(e).other.add(world.getEntity(otherB));
		mapper.create(e).other.add(world.getEntity(padding));
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertEquals(2, mapper.get(e).other.size()); // because skipping everything

		// target change
		mapper.get(e).other.add(world.getEntity(otherA));
		world.process();

		// kill link
		world.delete(e);
		world.process();
	}

	@Test
	public void multilink_automatic_field_bag_skip_target_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<MultiLinkSkipTargetCheck> mapper = world.getMapper(MultiLinkSkipTargetCheck.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(MultiLinkSkipTargetCheck.class, new LinkListener() {
			@Override
			public void onLinkEstablished(int sourceId, int targetId) {
				assertEquals(e, sourceId);
				assertEquals(-1, targetId);
			}

			@Override
			public void onLinkKilled(int sourceId, int targetId) {
				assertEquals(e, sourceId);
				assertEquals(-1, targetId);
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
		mapper.create(e).other.add(world.getEntity(otherB));
		mapper.create(e).other.add(world.getEntity(padding));
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertEquals(2, mapper.get(e).other.size()); // because skipping everything

		// target change
		mapper.get(e).other.add(world.getEntity(otherA));
		world.process();

		// kill link
		world.delete(e);
		world.process();
	}

	@Test
	public void multilink_automatic_field_bag_check_all_target_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<MultiLinkCheckAll> mapper = world.getMapper(MultiLinkCheckAll.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(MultiLinkCheckAll.class, new LinkListener() {
			@Override
			public void onLinkEstablished(int sourceId, int targetId) {
				assertEquals(e, sourceId);
				assertEquals(-1, targetId);
			}

			@Override
			public void onLinkKilled(int sourceId, int targetId) {
				assertEquals(e, sourceId);
				assertEquals(-1, targetId);
			}

			@Override
			public void onTargetDead(int sourceId, int deadTargetId) {
				assertEquals(e, sourceId);
				assertEquals(otherB, deadTargetId);
			}

			@Override
			public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
				fail();
			}
		});

		// establish link
		world.process();
		mapper.create(e).other.add(otherB);
		mapper.create(e).other.add(padding);
		world.process();

		// target dead
		world.delete(otherB);
		world.process();
		assertEquals(1, mapper.get(e).other.size()); // because NOT skipping everything

		// target change
		mapper.get(e).other.add(otherA);
		world.process();

		// kill link
		world.delete(e);
		world.process();
	}

	@Test
	public void mulltilink_explicit_field_int_test() {
		World world = new World(new WorldConfiguration()
			.setSystem(EntityLinkManager.class));

		final int padding = world.create();
		final int otherA = world.create();
		final int otherB = world.create();
		final int e = world.create();

		ComponentMapper<MuchOfEverything> mapper = world.getMapper(MuchOfEverything.class);

		EntityLinkManager elm = world.getSystem(EntityLinkManager.class);
		elm.register(MuchOfEverything.class, "otherId", new MyLinkListener(e, otherA, otherB));

		// establish link
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

	public static class MultiLinkSkip extends Component {
		@LinkPolicy(SKIP)
		public Bag<Entity> other = new Bag<Entity>();
	}

	public static class MultiLinkSkipTargetCheck extends Component {
		public Bag<Entity> other = new Bag<Entity>();
	}

	public static class MultiLinkCheckAll extends Component {
		@EntityId @LinkPolicy(CHECK_SOURCE_AND_TARGETS)
		public IntBag other = new IntBag();
	}


	public static class MuchOfEverything extends Component {
		@EntityId public IntBag intIds = new IntBag();
		@EntityId public int otherId;
		public Entity e;
		public Bag<Entity> entities = new Bag<Entity>();
		public int notMe;
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
		public void onLinkKilled(int sourceId, int targetId) {
			assertEquals(sourceId, e);
			assertEquals(targetId, otherA);
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