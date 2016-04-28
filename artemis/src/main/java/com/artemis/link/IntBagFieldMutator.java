package com.artemis.link;

import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.Aspect.all;

class IntBagFieldMutator implements MultiFieldMutator<IntBag, Component> {
	private final IntBag empty = new IntBag();
	private final EntitySubscription all;

	public IntBagFieldMutator(World world) {
		all = world.getAspectSubscriptionManager().get(all());
	}

	@Override
	public void validate(int sourceId, IntBag collection, LinkListener listener) {
		for (int i = 0; collection.size() > i; i++) {
			int id = collection.get(i);
			if (!all.getActiveEntityIds().get(id)) {
				collection.remove(i--);
				if (listener != null)
					listener.onTargetDead(sourceId, id);
			}
		}
	}

	@Override
	public IntBag read(Component c, Field f) {
		try {
			IntBag e = (IntBag) f.get(c);
			return (e != null) ? e : empty;
		} catch (ReflectionException exc) {
			throw new RuntimeException(exc);
		}
	}
}
