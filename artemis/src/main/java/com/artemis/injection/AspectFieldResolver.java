package com.artemis.injection;

import com.artemis.*;
import com.artemis.utils.reflect.Field;

import java.util.IdentityHashMap;

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

		Class type = field.getType();
		if (Aspect.class == type) {
//			world.getAspectSubscriptionManager()
		}

		return null;
	}

	private Aspect.Builder aspect(Field field) {

		return fields.get(field);
	}
}
