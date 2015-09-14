package com.artemis;

import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static com.artemis.utils.ConverterUtil.toIntBag;

public class AspectSubscriptionManager extends Manager {

	private final Map<Aspect.Builder, EntitySubscription> subscriptionMap;
	private Bag<EntitySubscription> subscriptions;

	private final IntBag addedIds = new IntBag();
	private final IntBag changedIds = new IntBag();
	private final IntBag deletedIds = new IntBag();

	private final AddedPerformer addedPerformer;
	private final ChangedPerformer changedPerformer;
	private final DeletedPerformer deletedPerformer;

	protected AspectSubscriptionManager() {
		subscriptionMap = new HashMap<Aspect.Builder, EntitySubscription>();
		subscriptions = new Bag<EntitySubscription>();

		addedPerformer = new AddedPerformer();
		changedPerformer = new ChangedPerformer();
		deletedPerformer = new DeletedPerformer();
	}

	public EntitySubscription get(Aspect.Builder builder) {
		EntitySubscription subscription = subscriptionMap.get(builder);
		return (subscription != null) ? subscription : createSubscription(builder);
	}

	private EntitySubscription createSubscription(Aspect.Builder builder) {
		EntitySubscription entitySubscription = new EntitySubscription(world, builder);
		subscriptionMap.put(builder, entitySubscription);
		subscriptions.add(entitySubscription);

		world.getEntityManager().synchronize(entitySubscription);

		return entitySubscription;
	}

	void process(BitSet added, BitSet changed, BitSet deleted) {
		toEntityIntBags(added, changed, deleted);

		check(deletedIds, deletedPerformer);
		check(addedIds, addedPerformer);
		check(changedIds, changedPerformer);

		Object[] subscribers = subscriptions.getData();
		for (int i = 0, s = subscriptions.size(); s > i; i++) {
			EntitySubscription subscriber = (EntitySubscription)subscribers[i];
			subscriber.process(addedIds, changedIds, deletedIds);
		}

		addedIds.setSize(0);
		changedIds.setSize(0);
		deletedIds.setSize(0);
	}

	private void toEntityIntBags(BitSet added, BitSet changed, BitSet deleted) {
		toIntBag(added, addedIds);
		toIntBag(changed, changedIds);
		toIntBag(deleted, deletedIds);

		added.clear();
		changed.clear();
		deleted.clear();
	}

	void processComponentIdentity(int id, BitSet componentBits) {
		Object[] subscribers = subscriptions.getData();
		for (int i = 0, s = subscriptions.size(); s > i; i++) {
			EntitySubscription subscriber = (EntitySubscription)subscribers[i];
			subscriber.processComponentIdentity(id, componentBits);
		}
	}

	/**
	 * Performs an action on each entity.
	 *
	 * @param entityIds contains the entities upon which the action will be performed
	 * @param performer the performer that carries out the action
	 */
	private void check(IntBag entityIds, Performer performer) {
		if (entityIds.isEmpty())
			return;

		notifyManagers(performer, entityIds);
	}

	/**
	 * Run performers on all managers.
	 *
	 * @param performer the performer to run
	 * @param entities the entity to pass as argument to the managers
	 */
	private void notifyManagers(Performer performer, IntBag entities) {
		Object[] data = world.managersBag.getData();
		for (int i = 0, s = world.managersBag.size(); s > i; i++) {
			performer.perform((Manager) data[i], entities);
		}
	}

	/**
	 * Runs {@link EntityObserver#deleted}.
	 */
	private static final class DeletedPerformer implements Performer {
		@Override
		public void perform(EntityObserver observer, IntBag entities) {
			observer.deleted(entities);
		}
	}

	/**
	 * Runs {@link EntityObserver#changed}.
	 */
	private static final class ChangedPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, IntBag entities) {
			observer.changed(entities);
		}
	}

	/**
	 * Runs {@link EntityObserver#added}.
	 */
	private static final class AddedPerformer implements Performer {

		@Override
		public void perform(EntityObserver observer, IntBag entities) {
			observer.added(entities);
		}
	}

	/**
	 * Calls methods on observers.
	 * <p>
	 * Only used internally to maintain clean code.
	 * </p>
	 */
	private interface Performer {

		/**
		 * Call a method on the observer with the entity as argument.
		 *
		 * @param observer the observer with the method to calll
		 * @param entities	the entities to pass as argument
		 */
		void perform(EntityObserver observer, IntBag entities);
	}
}
