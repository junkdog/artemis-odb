package com.artemis.link;

import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.Aspect.all;

public class IntBagFieldMutator implements MultiFieldMutator<IntBag, Component> {
	private final IntBag empty = new IntBag();
	private EntitySubscription all;

	@Override
	public void validate(int sourceId, IntBag ids, LinkListener listener) {
		for (int i = 0; ids.size() > i; i++) {
			int id = ids.get(i);
			if (!all.getActiveEntityIds().unsafeGet(id)) {
				ids.remove(i--);
				if (listener != null)
					listener.onTargetDead(sourceId, id);
			}
		}
	}

	@Override
	public IntBag read(Component c, Field f) {
		try {
			final boolean isNotAccessible = !f.isAccessible();
			if (isNotAccessible) {
				f.setAccessible(true);
			}
			IntBag e = (IntBag) f.get(c);
			if (isNotAccessible) {
				f.setAccessible(false);
			}
			return (e != null) ? e : empty;
		} catch (ReflectionException exc) {
			throw new RuntimeException(exc);
		}
	}

	@Override
	public void setWorld(World world) {
		all = world.getAspectSubscriptionManager().get(all());
	}
}
