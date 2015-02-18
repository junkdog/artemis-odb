package com.artemis;

import com.artemis.utils.Bag;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class AspectSubscriptionManager extends Manager {

	private final Map<Aspect.Builder, EntitySubscription> aspects;
	private Bag<EntitySubscription> subscriptions;

	public AspectSubscriptionManager() {
		aspects = new HashMap<Aspect.Builder, EntitySubscription>();
		subscriptions = new Bag<EntitySubscription>();
	}

	public boolean has(Aspect.Builder builder) {
		return aspects.containsKey(builder);
	}

	public Aspect get(Aspect.Builder builder) {
		return aspects.get(builder).aspect;
	}

	public void add(Aspect.Builder builder) {
		aspects.put(builder, new EntitySubscription(world, builder));
	}

	void process(Bag<Entity> added, Bag<Entity> changed, Bag<Entity> deleted) {
		Object[] subscribers = subscriptions.getData();
		for (int i = 0, s = subscriptions.size(); s > i; i++) {
			EntitySubscription subscriber = (EntitySubscription)subscribers[i];
			subscriber.process(added, changed, deleted);
		}
	}

	// TODO: heed later added subscriptions too
	void processComponentIdentity(int id, BitSet componentBits) {
		Object[] subscribers = subscriptions.getData();
		for (int i = 0, s = subscriptions.size(); s > i; i++) {
			EntitySubscription subscriber = (EntitySubscription)subscribers[i];
			subscriber.processComponentIdentity(id, componentBits);
		}
	}
}
