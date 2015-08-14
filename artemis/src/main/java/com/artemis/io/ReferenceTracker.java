package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ReferenceTracker {
	Bag<EntityReference> referenced = new Bag<EntityReference>();
	private Set<Class<?>> referencingTypes = new HashSet<Class<?>>();
	private Set<Field> referencingFields = new HashSet<Field>();

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
		Field[] fields = ClassReflection.getDeclaredFields(type);
		for (int i = 0; fields.length > i; i++) {
			Field f = fields[i];
			if (isReferencingEntity(f) && !referencingFields.contains(type)) {
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
}
