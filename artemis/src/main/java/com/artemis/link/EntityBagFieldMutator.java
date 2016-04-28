package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityBagFieldMutator implements MultiFieldMutator<Bag> {
	@Override
	public Bag read(Component c, Field f) {
		try {
			return (Bag) f.get(c);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
