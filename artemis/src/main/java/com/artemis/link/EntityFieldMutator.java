package com.artemis.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityFieldMutator implements UniFieldMutator {
	@Override
	public int read(Component c, Field f) {
		try {
			Entity e = (Entity) f.get(c);
			return (e != null) ? e.getId() : -1;
		} catch (ReflectionException exc) {
			throw new RuntimeException(exc);
		}
	}
}
