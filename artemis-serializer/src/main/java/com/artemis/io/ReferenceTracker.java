package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.ConverterUtil;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;

import com.artemis.utils.BitVector;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Maintains state of all component types which can reference other components.
 */
class ReferenceTracker {
	Bag<EntityReference> referenced = new Bag<EntityReference>();
	private Set<Class<?>> referencingTypes = new HashSet<Class<?>>();
	private Set<Field> referencingFields = new HashSet<Field>();

	private BitVector entityIds = new BitVector();
	private World world;

	ReferenceTracker(World world) {
		this.world = world;
	}

	void inspectTypes(World world) {
		clear();
		ComponentManager cm = world.getComponentManager();
		for (ComponentType ct : cm.getComponentTypes()) {
			inspectType(ct.getType());
		}
	}

	void inspectTypes(Collection<Class<? extends Component>> types) {
		clear();
		for (Class<?> component : types) {
			inspectType(component);
		}
	}

	private void clear() {
		referencingFields.clear();
		referencingTypes.clear();
		referenced.clear();
	}

	private void inspectType(Class<?> type) {
		if (referencingTypes.contains(type))
			return;

		Field[] fields = ClassReflection.getDeclaredFields(type);
		for (int i = 0; fields.length > i; i++) {
			Field f = fields[i];
			if (isReferencingEntity(f)) {
				referencingFields.add(f);
				referencingTypes.add(type);
				referenced.add(new EntityReference(type, f));
			}
		}
	}

	void addEntityReferencingComponent(Component c) {
		Class<? extends Component> componentClass = c.getClass();
		if (!referencingTypes.contains(componentClass))
			return;

		for (int i = 0, s = referenced.size(); s > i; i++) {
			EntityReference ref = referenced.get(i);

			if (ref.componentType == componentClass) {
				// a component can be referenced once per field
				// referencing another entity
				ref.operations.add(c);
			}
		}
	}


	void translate(Bag<Entity> translations) {
		for (EntityReference ref : referenced) {
			ref.translate(translations);
		}

		translations.clear();
	}

	EntityReference find(Class<?> componentType, String fieldName) {
		for (int i = 0, s = referenced.size(); s > i; i++) {
			EntityReference ref = referenced.get(i);
			if (ref.componentType.equals(componentType) && ref.field.getName().equals(fieldName))
				return ref;
		}

		throw new RuntimeException(
				componentType.getSimpleName() + "." + fieldName);
	}

	private boolean isReferencingEntity(Field f) {
		boolean explicitEntityId = f.getDeclaredAnnotation(EntityId.class) != null;
		Class type = f.getType();
		return (Entity.class == type)
				|| (Bag.class == type) // due to GWT limitations
				|| (int.class == type && explicitEntityId)
				|| (IntBag.class == type && explicitEntityId);
	}

	void preWrite(SaveFileFormat save) {
		entityIds.clear();

		ConverterUtil.toBitVector(save.entities, entityIds);
		boolean foundNew = true;

		BitVector bs = entityIds;

		while (foundNew) {
			foundNew = false;
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
				for (Field f : referencingFields) {
					foundNew |= findReferences(i, f, bs);
				}
			}
		}

		entityIds.toIntBag(save.entities);
	}

	private boolean findReferences(int entityId, Field f, BitVector referencedIds) {
		Component c = world.getEntity(entityId).getComponent(f.getDeclaringClass());
		if (c == null)
			return false;

		Class type = f.getType();
		try {
			if (type.equals(int.class)) {
				return updateReferenced((Integer)f.get(c), referencedIds);
			} else if (type.equals(Entity.class)) {
				return updateReferenced((Entity)f.get(c), referencedIds);
			} else if (type.equals(IntBag.class)) {
				return updateReferenced((IntBag)f.get(c), referencedIds);
			} else if (type.equals(Bag.class)) {
				return updateReferenced((Bag)f.get(c), referencedIds);
			} else {
				throw new RuntimeException("unknown type: " + type);
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean updateReferenced(Bag objects, BitVector referencedIds) {
		boolean updated = false;
		for (int i = 0; i < objects.size(); i++) {
			Object o = objects.get(i);
			if (o instanceof Entity) {
				updated |= updateReferenced((Entity)o, referencedIds);
			} else {
				return false;
			}
		}

		return updated;
	}

	private boolean updateReferenced(IntBag ids, BitVector referencedIds) {
		boolean updated = false;
		for (int i = 0; i < ids.size(); i++)
			updated |= updateReferenced(ids.get(i), referencedIds);

		return updated;
	}


	private boolean updateReferenced(Entity e, BitVector referencedIds) {
		return (e != null)
			? updateReferenced(e.getId(), referencedIds)
			: false;
	}

	private boolean updateReferenced(int entityId, BitVector referencedIds) {
		if (entityId > -1 && !referencedIds.get(entityId)) {
			referencedIds.set(entityId);
			return true;
		} else {
			return false;
		}
	}
}
