package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityBagFieldReader implements FieldReader {
	@Override
	public int readField(Component c, Field f, IntBag out) {
		try {
			Bag<Entity> entities = (Bag) f.get(c);
			if (entities != null) {
				out.setSize(0);
				for (int i = 0, s = entities.size(); s > i; i++) {
					out.add(entities.get(i).getId());
				}
			}
			return -1;
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
