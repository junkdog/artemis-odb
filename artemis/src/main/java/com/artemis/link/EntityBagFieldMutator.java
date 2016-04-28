package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityBagFieldMutator implements MultiFieldMutator {
	@Override
	public void read(Component c, Field f, IntBag out) {
		try {
			Bag<Entity> entities = (Bag) f.get(c);
			if (entities != null) {
				out.setSize(0);
				for (int i = 0, s = entities.size(); s > i; i++) {
					out.add(entities.get(i).getId());
				}
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
