package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;

import java.util.BitSet;

class MultiLinkSite extends LinkSite {
	MultiFieldMutator fieldMutator;

	private final Bag<BitSet> sourceToTargets = new Bag<BitSet>();

	protected MultiLinkSite(World world,
	                        ComponentType type,
	                        Field field) {

		super(world, type, field, LinkPolicy.Policy.CHECK_SOURCE);
	}

	@Override
	protected void check(int id) {
//		int oldTarget = sourceToTargets.get(id);
//		// -1 == not linked
//		int targets = fieldMutator.read(mapper.get(id), field);
//		if (!activeEntityIds.get(targets)) {
//			targets = -1;
//			fieldMutator.write(targets, mapper.get(id), field);
//		}
//
//		if (targets != oldTarget) {
//			sourceToTargets.set(id, targets);
//			if (oldTarget == -1) {
//				if (listener != null) listener.onLinkEstablished(id, targets);
//			} else {
//				if (listener != null) {
//					if (targets != -1) {
//						listener.onTargetChanged(id, targets, oldTarget);
//					} else {
//						listener.onTargetDead(id, oldTarget);
//					}
//				}
//			}
//		}
	}

	@Override
	protected void insert(int id) {
//		Object target = fieldMutator.read(mapper.get(id), field);
//		sourceToTarget.set(id, target);
//		if (target != -1 && listener != null)
//			listener.onLinkEstablished(id, target);
	}

	@Override
	protected void removed(int id) {
//		int target = sourceToTarget.size() > id
//			? sourceToTarget.get(id)
//			: -1;
//
//		if (target != -1) {
//			sourceToTarget.set(id, 0);
//			listener.onLinkKilled(id, target);
//		}
	}

	private BitSet ids(int sourceId) {
		BitSet ids = sourceToTargets.safeGet(sourceId);
		if (ids == null) {
			ids = new BitSet();
			sourceToTargets.set(sourceId, ids);
		}

		return ids;
	}
}
