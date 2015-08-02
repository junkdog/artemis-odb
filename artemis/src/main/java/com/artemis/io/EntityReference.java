package com.artemis.io;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityReference {
	public final Class<?> componentType;
	public final Field field;
	public final boolean isFieldInt;
	public transient final Bag<Component> operations = new Bag<Component>();

	EntityReference(Class<?> componentType, Field field) {
		this.componentType = componentType;
		this.field = field;
		this.isFieldInt = int.class == field.getType();
	}

	void translate(Bag<Entity> translatedIds) {
		for (Component c : operations) {
			try {
				if (isFieldInt) {
					int oldId = ((Integer)field.get(c)).intValue();
					field.set(c, translatedIds.get(oldId).id);
				} else {
					int oldId = ((Entity)field.get(c)).id;
					field.set(c, translatedIds.get(oldId));
				}
			} catch (ReflectionException e) {
				e.printStackTrace();
			}
		}

		operations.clear();
	}
}
