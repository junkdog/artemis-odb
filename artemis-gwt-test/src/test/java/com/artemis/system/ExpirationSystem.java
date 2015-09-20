package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.component.ComponentY;
import com.artemis.systems.DelayedEntityProcessingSystem;
import com.artemis.utils.Bag;

public class ExpirationSystem extends DelayedEntityProcessingSystem {
	// don't do this IRL
	public Bag<Float> deltas = new Bag<Float>();
	public int expiredLastRound;

	@SuppressWarnings("unchecked")
	public ExpirationSystem() {
		super(Aspect.all(ComponentY.class));
	}
	
	@Override
	protected void inserted(int entityId) {
		deltas.set(entityId, 1f);
		super.inserted(entityId);
	}
	
	@Override
	protected float getRemainingDelay(int e) {
		return deltas.get(e);
	}

	@Override
	protected void processDelta(int e, float accumulatedDelta) {
		float remaining = deltas.get(e);
		remaining -=  accumulatedDelta;
		offerDelay(remaining);
		deltas.set(e, remaining);
	}

	@Override
	protected void processExpired(int e) {
		expiredLastRound++;
		deltas.set(e, null);
		world.deleteEntity(e);
	}
	
	@Override
	protected void begin() {
		expiredLastRound = 0;
	}
}