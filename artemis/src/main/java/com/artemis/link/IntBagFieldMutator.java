package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class IntBagFieldMutator implements MultiFieldMutator<IntBag> {
	@Override
	public IntBag read(Component c, Field f) {
		try {
			return  (IntBag) f.get(c);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
