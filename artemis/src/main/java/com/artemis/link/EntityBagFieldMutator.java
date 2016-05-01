package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import static com.artemis.Aspect.all;

class EntityBagFieldMutator implements MultiFieldMutator<Bag<Entity>, Component> {
	private final Bag<Entity> empty = new Bag<Entity>();
	private EntitySubscription all;

	@Override
	public void validate(int sourceId, Bag<Entity> entities, LinkListener listener) {
		for (int i = 0; entities.size() > i; i++) {
			Entity e = entities.get(i);
			if (!all.getActiveEntityIds().get(e.getId())) {
				entities.remove(i--);
				if (listener != null)
					listener.onTargetDead(sourceId, e.getId());
			}
		}
	}

	@Override
	public Bag<Entity> read(Component c, Field f) {
		try {
			Bag<Entity> e = (Bag<Entity>) f.get(c);
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
