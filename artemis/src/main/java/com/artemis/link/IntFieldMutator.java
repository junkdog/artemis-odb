package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class IntFieldMutator implements UniFieldMutator {
	@Override
	public int read(Component c, Field f) {
		try {
			return (Integer) f.get(c);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
