package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityFieldReader implements FieldReader {
	@Override
	public int readField(Component c, Field f, IntBag out) {
		try {
			Entity e = (Entity) f.get(c);
			return (e != null) ? e.getId() : -1;
		} catch (ReflectionException exc) {
			throw new RuntimeException(exc);
		}
	}
}
