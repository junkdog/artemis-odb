package com.artemis.link;

import com.artemis.Component;
import com.artemis.World;
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

	@Override
	public void write(int value, Component c, Field f) {
		try {
			f.set(c, value);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setWorld(World world) {}
}
