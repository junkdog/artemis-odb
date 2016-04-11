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
	private final EntityManager em;
	private final ComponentManager cm;
	private final BatchChangeProcessor batchProcessor;
	private final BitSet additions;
	private final BitSet removals;
	private final Bag<TransmuteOperation> operations;

	private final BitSet bs;

	EntityTransmuter(World world, BitSet additions, BitSet removals) {
		em = world.getEntityManager();
		cm = world.getComponentManager();
		batchProcessor = world.batchProcessor;
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
		if (batchProcessor.isDeleted(entityId))
			return;

		if (!em.isActive(entityId))
			throw new RuntimeException("Issued transmute on deleted " + entityId);

		TransmuteOperation operation = getOperation(entityId);
		operation.perform(entityId);
		em.setIdentity(entityId, operation.compositionId);

		batchProcessor.changed.set(entityId);
	}

	void transmuteNoOperation(int entityId) {
		if (batchProcessor.isDeleted(entityId))
			return;

		if (!em.isActive(entityId))
			throw new RuntimeException("Issued transmute on deleted " + entityId);

		TransmuteOperation operation = getOperation(entityId);
		em.setIdentity(entityId, operation.compositionId);
		batchProcessor.changed.set(entityId);
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

	TransmuteOperation getOperation(int entityId) {
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
		int compositionId = em.compositionIdentity(bs);
		return new TransmuteOperation(cm,
				compositionId, getAdditions(componentBits), getRemovals(componentBits));
	}

	private Bag<ComponentMapper> getAdditions(BitSet origin) {
		ComponentTypeFactory tf = cm.typeFactory;
		Bag<ComponentMapper> types = new Bag<ComponentMapper>();
		for (int i = additions.nextSetBit(0); i >= 0; i = additions.nextSetBit(i + 1)) {
			if (!origin.get(i))
				types.add(cm.getMapper(tf.getTypeFor(i).getType()));
		}

		return types;
	}

	private Bag<ComponentMapper> getRemovals(BitSet origin) {
		ComponentTypeFactory tf = cm.typeFactory;
		Bag<ComponentMapper> types = new Bag<ComponentMapper>();
		for (int i = removals.nextSetBit(0); i >= 0; i = removals.nextSetBit(i + 1)) {
			if (origin.get(i))
				types.add(cm.getMapper(tf.getTypeFor(i).getType()));
		}

		return types;
	}

	@Override
	public String toString() {
		return "EntityTransmuter(add=" + additions + " remove=" + removals + ")";
	}

	static class TransmuteOperation {
		private final ComponentMapper[] additions;
		private final ComponentMapper[] removals;

		private ComponentManager cm;
		public final short compositionId;

		public TransmuteOperation(ComponentManager cm,
		                          int compositionId,
		                          ComponentMapper[] additions,
		                          ComponentMapper[] removals) {
			this.cm = cm;
			this.compositionId = (short) compositionId;
			this.additions = additions;
			this.removals = removals;
		}

		public TransmuteOperation(ComponentManager cm,
		                          int compositionId,
		                          Bag<ComponentMapper> additions,
		                          Bag<ComponentMapper> removals) {
			this.cm = cm;
			this.compositionId = (short) compositionId;
			this.additions = new ComponentMapper[additions.size()];
			this.removals = new ComponentMapper[removals.size()];

			for (int i = 0, s = additions.size(); s > i; i++) {
				this.additions[i] = additions.get(i);
			}

			for (int i = 0, s = removals.size(); s > i; i++) {
				this.removals[i] = removals.get(i);
			}
		}

		public void perform(int entityId) {
			for (int i = 0, s = additions.length; s > i; i++) {
				additions[i].internalCreate(entityId);
			}

			for (int i = 0, s = removals.length; s > i; i++) {
					removals[i].internalRemove(entityId);
			}
		}

		Bag<Class<? extends Component>> getAdditions(Bag<Class<? extends Component>> out) {
			for (int i = 0, s = additions.length; s > i; i++) {
				out.add(additions[i].getType().getType());
			}

			return out;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("TransmuteOperation(");

			if (additions.length > 0) {
				sb.append("add={");
				String delim = "";
				for (ComponentMapper mapper : additions) {
					sb.append(delim).append(mapper.getType().getType().getSimpleName());
					delim = ", ";
				}
				sb.append("}");
			}

			if (removals.length > 0) {
				if (additions.length > 0)
					sb.append(" ");

				sb.append("remove={");
				String delim = "";
				for (ComponentMapper mapper : removals) {
					sb.append(delim).append(mapper.getType().getType().getSimpleName());
					delim = ", ";
				}
				sb.append("}");
			}
			sb.append(")");

			return sb.toString();
		}
	}
}
