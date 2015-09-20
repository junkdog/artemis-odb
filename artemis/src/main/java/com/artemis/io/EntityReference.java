package com.artemis.io;

import com.artemis.Component;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

class EntityReference {
	public final Class<?> componentType;
	public final Field field;
	public final FieldType fieldType;
	public transient final Bag<Component> operations = new Bag<Component>();

	EntityReference(Class<?> componentType, Field field) {
		this.componentType = componentType;
		this.field = field;
		this.fieldType = FieldType.resolve(field);
	}

	void translate(IntBag translatedIds) {
		for (Component c : operations)
			fieldType.translate(c, field, translatedIds);

		operations.clear();
	}

	@Override
	public String toString() {
		return "EntityReference{" +
				componentType.getSimpleName() +
				"." + field.getName() +
				" (" + fieldType +
				"), operations=" + operations.size() +
				'}';
	}

	enum FieldType {
		INT {
			void translate(Component c, Field field, IntBag translatedIds) {
				try {
					int oldId = ((Integer)field.get(c)).intValue();
					field.set(c, translatedIds.get(oldId));
				} catch (ReflectionException e) {
					throw  new RuntimeException(e);
				}
			}
		},
		INT_BAG {
			void translate(Component c, Field field, IntBag translatedIds) {
				try {
					IntBag bag = (IntBag) field.get(c);
					for (int i = 0, s = bag.size(); s > i; i++) {
						int oldId = bag.get(i);
						bag.set(i, translatedIds.get(oldId));
					}
				} catch (ReflectionException e) {
					throw  new RuntimeException(e);
				}
			}
		},
		ENTITY {
			void translate(Component c, Field field, IntBag translatedIds) {
				try {
					int oldId = field.getInt(c);
					field.set(c, translatedIds.get(oldId));
				} catch (ReflectionException e) {
					throw new RuntimeException(e);
				}
			}
		},
		ENTITY_BAG {
			void translate(Component c, Field field, IntBag translatedIds) {
				try {
					IntBag bag = (IntBag) field.get(c);
					for (int i = 0, s = bag.size(); s > i; i++) {
						int e = bag.get(i);
						bag.set(i, translatedIds.get(e));
					}
				} catch (ReflectionException e) {
					throw new RuntimeException(e);
				}
			}
		};

		abstract void translate(Component c, Field field, IntBag translatedIds);

		static FieldType resolve(Field f) {
			Class type = f.getType();
			if (int.class == type)
				return INT;
			else if (IntBag.class == type)
				return INT_BAG;
			else if (Bag.class == type)
				return ENTITY_BAG;
			else
				throw new RuntimeException("missing case: " + type.getSimpleName());
		}
	}
}
