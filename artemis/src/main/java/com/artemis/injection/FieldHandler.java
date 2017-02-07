package com.artemis.injection;

import com.artemis.InjectionException;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;

import java.util.Map;

/**
 * FieldHandler provides dependency-values to an {@link com.artemis.injection.Injector}
 * by sequentially iterating over a list of registered {@link FieldResolver}.
 * <p>
 * The method {@link #resolve(Object, Class, Field)} will return the first non-null value provided by
 * {@link FieldResolver#resolve(Object, Class, Field)}, or null if no resolver returned a valid value.
 * </p>
 * <p>
 * During {@link World} construction, after systems and managers have been created, {@link #initialize(World, Map)}
 * will be called for each registered {@link FieldResolver}
 * </p>
 * <p>
 * If {@link FieldResolver} implements {@link UseInjectionCache}, {@link UseInjectionCache#setCache(InjectionCache)}
 * will be called prior to  {@link FieldResolver#initialize(World)} being called, availing the {@link InjectionCache}
 * used by this handler.
 * </p>
 *
 * @author Snorre E. Brekke
 */
public class FieldHandler {
	private InjectionCache cache;
	protected Bag<FieldResolver> fieldResolvers;

	/**
	 * Constructs a new FieldHandler with the provided fieldResolvers. This constructor should be used when full
	 * control over the {@link FieldResolver} order is required.
	 * <p>
	 * For Artemis to function correctly, {@link ArtemisFieldResolver} should be added somewhere in the bag, or
	 * added via {@link #addFieldResolver(FieldResolver)} prior to world construction.
	 * </p>
	 *
	 * @param cache          used for better reflection-speed.
	 * @param fieldResolvers bag of fieldresolver this FieldHandler should use.
	 * @see ArtemisFieldResolver
	 */
	public FieldHandler(InjectionCache cache, Bag<FieldResolver> fieldResolvers) {
		this.cache = cache;
		this.fieldResolvers = fieldResolvers;
	}


	/**
	 * Constructs a ned FieldHandler with an {@link ArtemisFieldResolver} and {@link WiredFieldResolver}
	 * already registered, which can resolve {@link com.artemis.ComponentMapper}, {@link com.artemis.BaseSystem}
	 * and {@link com.artemis.Manager} types registered in the {@link World}
	 * {@link com.artemis.annotations.Wire}.
	 *
	 * @param cache used for better reflection-speed.
	 * @see ArtemisFieldResolver
	 */
	public FieldHandler(InjectionCache cache) {
		this.fieldResolvers = new Bag(FieldResolver.class);
		this.cache = cache;
		// the order FieldResolvers are added is relevant, we want to prioritize @Wired fields
		addFieldResolver(new WiredFieldResolver());
		addFieldResolver(new ArtemisFieldResolver());
		addFieldResolver(new AspectFieldResolver());
	}

	/**
	 * During {@link World} construction, after systems and managers have been created, {@link #initialize(World, Map)}
	 * will be called for each registered {@link FieldResolver}
	 * </p>
	 * <p/>
	 * If {@link FieldResolver} implements {@link UseInjectionCache}, {@link UseInjectionCache#setCache(InjectionCache)}
	 * will be called prior to  {@link FieldResolver#initialize(World)} being called, availing the {@link InjectionCache}
	 * used by this handler.
	 *
	 * @param world the world this FieldHandler is being used for
	 * @throws com.artemis.WorldConfigurationException when injector has no way to deal with injectables.
	 */
	public void initialize(World world, Map<String, Object> injectables) {

		boolean fieldResolverFound = false;

		for (int i = 0, s = fieldResolvers.size(); i < s; i++) {
			FieldResolver fieldResolver = fieldResolvers.get(i);
			if (ClassReflection.isInstance(UseInjectionCache.class, fieldResolver)) {
				((UseInjectionCache) fieldResolver).setCache(cache);
			}

			if (ClassReflection.isInstance(PojoFieldResolver.class, fieldResolver)) {
				((PojoFieldResolver) fieldResolver).setPojos(injectables);
				fieldResolverFound = true;
			}

			fieldResolver.initialize(world);
		}

		if ( injectables != null && !injectables.isEmpty() && !fieldResolverFound )
		{
			throw new InjectionException("FieldHandler lacks resolver capable of dealing with your custom injectables. Register a WiredFieldResolver or PojoFieldResolver with your FieldHandler.");
		}
	}

	/**
	 * Returns the first non-null value provided by
	 * {@link FieldResolver#resolve(Object, Class, Field)}, or null if no resolver returned a valid value.
	 *
	 * @param fieldType class of the field
	 * @param field     field for which a value should be resolved
	 * @return a non-null value if any {@link FieldResolver} could provide an instance
	 * for the {@code field}, null if the {@code field} could not be resolved
	 */
	public Object resolve(Object target, Class<?> fieldType, Field field) {
		for (int i = 0, s = fieldResolvers.size(); i < s; i++) {
			Object resolved = fieldResolvers.get(i).resolve(target, fieldType, field);
			if (resolved != null) {
				return resolved;
			}
		}
		return null;
	}

	/**
	 * Adds a {@link FieldResolver} to this handler. Resolvers added first, will be used first for resolving fields,
	 * so the order of add operations is significant.
	 *
	 * @param fieldResolver is added to this FieldHandler fieldresolver-list
	 */
	public final void addFieldResolver(FieldResolver fieldResolver) {
		fieldResolvers.add(fieldResolver);
	}

	public Bag<FieldResolver> getFieldResolvers() {
		return fieldResolvers;
	}
}
