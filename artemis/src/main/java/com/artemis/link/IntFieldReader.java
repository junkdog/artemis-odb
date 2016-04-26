package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class IntFieldReader implements FieldReader {
	@Override
	public int readField(Component c, Field f, IntBag out) {
		try {
			return (Integer) f.get(c);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
