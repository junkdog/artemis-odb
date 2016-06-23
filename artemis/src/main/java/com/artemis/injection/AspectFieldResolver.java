package com.artemis.injection;

import com.artemis.*;
import com.artemis.annotations.AspectDescriptor;
import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.Field;

import java.util.IdentityHashMap;

import static com.artemis.Aspect.all;

/**
 * <p>Resolves the following aspect-related types:</p>
 * <ul>
 *     <li>{@link com.artemis.Aspect}</li>
 *     <li>{@link com.artemis.Aspect.Builder}</li>
 *     <li>{@link com.artemis.EntitySubscription}</li>
 *     <li>{@link com.artemis.EntityTransmuter}</li>
 * </ul>
 *
 * @author Snorre E. Brekke
 * @author Adrian Papari
 */
public class AspectFieldResolver implements FieldResolver {

	private World world;

	private IdentityHashMap<Field, Aspect.Builder> fields = new IdentityHashMap<Field, Aspect.Builder>();

	@Override
	public void initialize(World world) {
		this.world = world;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object resolve(Class<?> fieldType, Field field) {
		Aspect.Builder aspect = aspect(field);
		if (aspect == null)
			return null;

		Class type = field.getType();
		if (Aspect.class == type) {
			return world.getAspectSubscriptionManager().get(aspect).getAspect();
		} else if (Aspect.Builder.class == type) {
			return aspect;
		} else if (EntityTransmuter.class == type) {
			return new EntityTransmuter(world, aspect);
		} else if (EntitySubscription.class == type) {
			return world.getAspectSubscriptionManager().get(aspect);
		} else if (Archetype.class == type) {
			return new ArchetypeBuilder()
				.add(descriptor(field).all())
				.build(world);
		}

		return null;
	}

	private Aspect.Builder aspect(Field field) {
		if (!fields.containsKey(field)) {
			AspectDescriptor descriptor = descriptor(field);

			if (descriptor != null) {
				fields.put(field, toAspect(descriptor));
			} else {
				fields.put(field, null);
			}
		}

		return  fields.get(field);
	}

	private AspectDescriptor descriptor(Field field) {
		Annotation anno	= field.getDeclaredAnnotation(AspectDescriptor.class);
		return (anno != null)
			? anno.getAnnotation(AspectDescriptor.class)
			: null;
	}

	private Aspect.Builder toAspect(AspectDescriptor ad) {
		return all(ad.all()).one(ad.one()).exclude(ad.exclude());
	}
}
