package com.artemis;

import java.util.HashMap;
import java.util.Map;

public class AspectSubscriptionManager extends Manager {

	private final Map<Aspect.Builder, EntitySubscription> aspects;

	public AspectSubscriptionManager() {
		aspects = new HashMap<Aspect.Builder, EntitySubscription>();
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
}
