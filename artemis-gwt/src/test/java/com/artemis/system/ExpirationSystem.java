package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.component.ComponentY;
import com.artemis.systems.DelayedEntityProcessingSystem;
import com.artemis.utils.Bag;

public class ExpirationSystem extends DelayedEntityProcessingSystem {
	// don't do this IRL
	public Bag<Float> deltas = new Bag<Float>();
	public int expiredLastRound;

	@SuppressWarnings("unchecked")
	public ExpirationSystem() {
		super(Aspect.getAspectForAll(ComponentY.class));
	}
	
	@Override
	protected void inserted(Entity e) {
		deltas.set(e.getId(), 1f);
		super.inserted(e);
	}
	
	@Override
	protected float getRemainingDelay(Entity e) {
		return deltas.get(e.getId());
	}

	@Override
	protected void processDelta(Entity e, float accumulatedDelta) {
		float remaining = deltas.get(e.getId());
		remaining -=  accumulatedDelta;
		offerDelay(remaining);
		deltas.set(e.getId(), remaining);
	}

	@Override
	protected void processExpired(Entity e) {
		expiredLastRound++;
		deltas.set(e.getId(), null);
		e.deleteFromWorld();
	}
	
	@Override
	protected void begin() {
		expiredLastRound = 0;
	}
}