package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;

import java.util.BitSet;

import static com.artemis.Aspect.all;

class UniLinkSite extends LinkSite {
	UniFieldMutator entityReader;

	private final IntBag sourceToTarget = new IntBag();
	private final BitSet activeEntityIds;

	protected UniLinkSite(World world,
	                      ComponentType type,
	                      Field field) {

		super(world, type, field);
		activeEntityIds = world.getAspectSubscriptionManager().get(all()).getActiveEntityIds();
	}

	@Override
	protected void check(int id) {
		int oldTarget = sourceToTarget.get(id);
		// -1 == not linked
		int target = entityReader.read(mapper.get(id), field);
		if (!activeEntityIds.get(target))
			target = -1;

		if (target != oldTarget)
			sourceToTarget.set(id, target);
			if (oldTarget == -1) {
				if (listener != null) listener.onLinkEstablished(id, target);
			} else {
				if (listener != null) {
					if (target == -1) {
						listener.onTargetChanged(id, target, oldTarget);
					} else {
						listener.onTargetDead(id, oldTarget);
					}
				}
			}
	}

	@Override
	protected void insert(int id) {
		int target = entityReader.read(mapper.get(id), field);
		sourceToTarget.set(id, target);
		if (target != -1 && listener != null)
			listener.onLinkEstablished(id, target);
	}

	@Override
	protected void removed(int id) {
		int target = sourceToTarget.size() > id
			? sourceToTarget.get(id)
			: -1;

		if (target != -1) {
			sourceToTarget.set(id, 0);
			listener.onLinkKilled(id);
		}
	}
}
