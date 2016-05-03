package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;

class UniLinkSite extends LinkSite {
	UniFieldMutator fieldMutator;

	private final IntBag sourceToTarget = new IntBag();

	protected UniLinkSite(World world,
	                      ComponentType type,
	                      Field field) {

		super(world, type, field, LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS);
	}

	@Override
	protected void check(int id) {
		// -1 == not linked
		int target = fieldMutator.read(mapper.get(id), field);
		if (target != -1 && !activeEntityIds.get(target)) {
			target = -1;
			fieldMutator.write(target, mapper.get(id), field);
		}

		int oldTarget = sourceToTarget.get(id);
		if (target != oldTarget) {
			sourceToTarget.set(id, target);
			if (oldTarget == -1) {
				if (listener != null) listener.onLinkEstablished(id, target);
			} else {
				if (listener != null) {
					if (target != -1) {
						listener.onTargetChanged(id, target, oldTarget);
					} else {
						listener.onTargetDead(id, oldTarget);
					}
				}
			}
		}
	}

	@Override
	protected void insert(int id) {
		int target = fieldMutator.read(mapper.get(id), field);
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
			sourceToTarget.set(id, -1);
			if (listener != null) listener.onLinkKilled(id, target);
		}
	}
}
