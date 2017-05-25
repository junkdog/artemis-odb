package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.Aspect.all;

public class EntityBagFieldMutator<T extends Entity> implements MultiFieldMutator<Bag<T>, Component> {
	private final Bag<T> empty = new Bag<>();
	private EntitySubscription all;

	@Override
	public void validate(int sourceId, Bag<T> entities, LinkListener listener) {
		for (int i = 0; entities.size() > i; i++) {
			Entity e = entities.get(i);
			if (!all.getActiveEntityIds().unsafeGet(e.getId())) {
				entities.remove(i--);
				if (listener != null)
					listener.onTargetDead(sourceId, e.getId());
			}
		}
	}

	@Override
	public Bag<T> read(Component c, Field f) {
		try {
			Bag<T> e = (Bag<T>) f.get(c);
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
