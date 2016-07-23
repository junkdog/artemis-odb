package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;

import com.artemis.utils.BitVector;
import java.util.HashMap;
import java.util.Map;

import static com.artemis.Aspect.all;

/**
 * <p>Manages all instances of {@link EntitySubscription}.</p>
 *
 * <p>Entity subscriptions are automatically updated during {@link com.artemis.World#process()}.
 * Any {@link com.artemis.EntitySubscription.SubscriptionListener listeners}
 * are informed when entities are added or removed.</p>
 *
 * @see EntitySubscription
 */
@SkipWire
public class AspectSubscriptionManager extends BaseSystem {

	private final Map<Aspect.Builder, EntitySubscription> subscriptionMap;
	private final Bag<EntitySubscription> subscriptions = new Bag(EntitySubscription.class);

	private final IntBag changed = new IntBag();
	private final IntBag deleted = new IntBag();

	protected AspectSubscriptionManager() {
		subscriptionMap = new HashMap<Aspect.Builder, EntitySubscription>();
	}

	@Override
	protected void processSystem() {}

	@Override
	protected void initialize() {
		// making sure subscription 1 matches all entities
		get(all());
	}

	/**
	 * <p>Gets the entity subscription for the {@link Aspect}.
	 * Subscriptions are only created once per aspect.</p>
	 *
	 * @param builder Aspect to match.
	 * @return {@link EntitySubscription} for aspect.
	 */
	public EntitySubscription get(Aspect.Builder builder) {
		EntitySubscription subscription = subscriptionMap.get(builder);
		return (subscription != null) ? subscription : createSubscription(builder);
	}

	private EntitySubscription createSubscription(Aspect.Builder builder) {
		EntitySubscription entitySubscription = new EntitySubscription(world, builder);
		subscriptionMap.put(builder, entitySubscription);
		subscriptions.add(entitySubscription);

		world.getComponentManager().synchronize(entitySubscription);
		return entitySubscription;
	}

	/**
	 * Informs all listeners of added, changedBits and deletedBits changes.
	 *
	 * Two types of listeners:
	 * {@see EntityObserver} implementations are guaranteed to be called back in order of system registration.
	 * {@see com.artemis.EntitySubscription.SubscriptionListener}, where order can vary (typically ordinal, except
	 * for subscrip1tions created in process, initialize instead of setWorld).
     *
	 * {@link com.artemis.EntitySubscription.SubscriptionListener#inserted(IntBag)}
	 * {@link com.artemis.EntitySubscription.SubscriptionListener#removed(IntBag)}
	 *
	 * Observers are called before Subscriptions, which means managerial tasks get artificial priority.
	 *
	 * @param changedBits Entities with changedBits composition or state.
	 * @param deletedBits Entities removed from world.
	 */
	void process(BitVector changedBits, BitVector deletedBits) {
		toEntityIntBags(changedBits, deletedBits);

		// note: processAll != process
		subscriptions.get(0).processAll(changed, deleted);

		for (int i = 1, s = subscriptions.size(); s > i; i++) {
			subscriptions.get(i).process(changed, deleted);
		}
	}

	private void toEntityIntBags(BitVector changed, BitVector deleted) {
		changed.toIntBagIdCid(world.getComponentManager(), this.changed);
		deleted.toIntBag(this.deleted);

		changed.clear();
		deleted.clear();
	}

	void processComponentIdentity(int id, BitVector componentBits) {
		for (int i = 0, s = subscriptions.size(); s > i; i++) {
			subscriptions.get(i).processComponentIdentity(id, componentBits);
		}
	}

	/**
	 * Gets the active list of all current entity subscriptions. Meant to assist
	 * in tooling/debugging.
	 *
	 * @return All active subscriptions.
	 */
	public ImmutableBag<EntitySubscription> getSubscriptions() {
		return subscriptions;
	}
}
