package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class IntBagFieldMutator implements MultiFieldMutator {
	@Override
	public void read(Component c, Field f, IntBag out) {
		try {
			IntBag ids = (IntBag) f.get(c);
			if (ids != null) {
				out.setSize(0);
				out.addAll(ids);
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}
}
