package com.artemis;

import com.artemis.utils.Bag;

import java.util.BitSet;

/**
 * Fastest way of changing entity component compositions. Primarily useful when
 * bootstrapping entities over several different systems or when
 * dealing with many entities at the same time (light particle systems etc).
 * <p>
 * Given a set of component additions/removals: for each encountered
 * compositionId, cache the calculated new compositionId. This extends
 * the performance benefits introduced with
 * {@link com.artemis.Archetype Archetypes} in 0.7.0 to carry over to existing entities.
 * </p>
 * @see com.artemis.EntityTransmuterFactory
 */
public final class EntityTransmuter {
	private final World world;
	private final BitSet additions;
	private final BitSet removals;
	private final Bag<TransmuteOperation> operations;

	private final BitSet bs;

	EntityTransmuter(World world, BitSet additions, BitSet removals) {
		this.world = world;
		this.additions = additions;
		this.removals = removals;
		operations = new Bag<TransmuteOperation>();

		bs = new BitSet();
	}

	/**
	 * <p>Apply on target entity. Does nothing if entity has been scheduled for
	 * deletion.</p>
	 *
	 * <p>Transmuter will add components by replacing and retire pre-existing components.</p>
	 *
	 * @param entityId target entity id
	 */
	public void transmute(int entityId) {
		if (world.editPool.isPendingDeletion(entityId))
			return;

		if (!world.getEntityManager().isActive(entityId))
			throw new RuntimeException("Issued transmute on deleted " + entityId);

		TransmuteOperation operation = getOperation(entityId);
		operation.perform(entityId, world.getComponentManager());
		world.getEntityManager().setIdentity(entityId, operation.compositionId);

		world.changed.set(entityId);
	}

	/**
	 * Apply on target entity.
	 *
	 * Transmuter will add components by replacing and retire pre-existing components.
	 *
	 * @param e target entity.
	 */
	public void transmute(Entity e) {
		transmute(e.id);
	}

	private TransmuteOperation getOperation(int entityId) {
		if (world.editPool.isEdited(entityId)) {
			world.editPool.processAndRemove(entityId);
		}

		EntityManager em = world.getEntityManager();
		int compositionId = em.getIdentity(entityId);
		TransmuteOperation operation = operations.safeGet(compositionId);
		if (operation == null) {
			operation = createOperation(em.componentBits(entityId));
			operations.set(compositionId, operation);
		}
		return operation;
	}

	private TransmuteOperation createOperation(BitSet componentBits) {
		bs.clear();
		bs.or(componentBits);
		bs.or(additions);
		bs.andNot(removals);
		int compositionId = world.getEntityManager().compositionIdentity(bs);
		return new TransmuteOperation(
				compositionId, getAdditions(componentBits), getRemovals(componentBits));
	}

	private Bag<ComponentType> getAdditions(BitSet origin) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		Bag<ComponentType> types = new Bag<ComponentType>();
		for (int i = additions.nextSetBit(0); i >= 0; i = additions.nextSetBit(i + 1)) {
			if (!origin.get(i))
				types.add(tf.getTypeFor(i));
		}

		return types;
	}

	private Bag<ComponentType> getRemovals(BitSet origin) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		Bag<ComponentType> types = new Bag<ComponentType>();
		for (int i = removals.nextSetBit(0); i >= 0; i = removals.nextSetBit(i + 1)) {
			if (origin.get(i))
				types.add(tf.getTypeFor(i));
		}

		return types;
	}

	@Override
	public String toString() {
		return "EntityTransmuter(add=" + additions + " remove=" + removals + ")";
	}

	static class TransmuteOperation {
		private Bag<ComponentType> additions;
		private Bag<ComponentType> removals;
		public final int compositionId;

		public TransmuteOperation(int compositionId, Bag<ComponentType> additions, Bag<ComponentType> removals) {
			this.compositionId = compositionId;
			this.additions = additions;
			this.removals = removals;
		}

		public void perform(int entityId, ComponentManager cm) {
			for (int i = 0, s = additions.size(); s > i; i++)
				cm.create(entityId, additions.get(i));

			for (int i = 0, s = removals.size(); s > i; i++)
				cm.removeComponent(entityId, removals.get(i));
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("TransmuteOperation(");

			if (additions.size() > 0) {
				sb.append("add={");
				String delim = "";
				for (ComponentType ct : additions) {
					sb.append(delim).append(ct.getType().getSimpleName());
					delim = ", ";
				}
				sb.append("}");
			}

			if (removals.size() > 0) {
				if (additions.size() > 0)
					sb.append(" ");

				sb.append("remove={");
				String delim = "";
				for (ComponentType ct : removals) {
					sb.append(delim).append(ct.getType().getSimpleName());
					delim = ", ";
				}
				sb.append("}");
			}
			sb.append(")");

			return sb.toString();
		}
	}
}
