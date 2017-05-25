package com.artemis.injection;

import com.artemis.InjectionException;
import com.artemis.World;

import java.util.Map;

/**
 * <p>API used by {@link com.artemis.World} to inject objects annotated with {@link com.artemis.annotations.Wire} with
 * dependencies. An injector injects {@link com.artemis.ComponentMapper}, {@link com.artemis.BaseSystem} and {@link com.artemis
 * .Manager} types into systems and managers.
 * </p>
 * <p>To inject arbitrary types, use registered through {@link com.artemis.WorldConfiguration#register}.</p>
 * <p>To customize the injection-strategy for arbitrary types further, registered a custom {@link com.artemis.injection.FieldHandler}
 * with custom one or more {@link com.artemis.injection.FieldResolver}.</p>
 *
 * @author Snorre E. Brekke
 * @see FieldHandler
 */
public interface Injector {

	/**
	 * Programmatic retrieval of registered objects. Useful when
	 * full injection isn't necessary.
	 *
	 * @param id Name or class name.
	 * @return the requested object, or null if not found
	 *
	 * @see com.artemis.WorldConfiguration#register(String, Object)
	 */
	<T> T getRegistered(String id);

	/**
	 * Programmatic retrieval of registered objects. Useful when
	 * full injection isn't necessary. This method internally
	 * calls {@link #getRegistered(String)}, with the class name
	 * as parameter.
	 *
	 * @param id Uniquely registered instance, identified by class..
	 * @return the requested object, or null if not found
	 *
	 * @see com.artemis.WorldConfiguration#register(Object)
	 */
	<T> T getRegistered(Class<T> id);

	/**
	 * @param world       this Injector will be used for
	 * @param injectables registered via {@link com.artemis.WorldConfiguration#register}
	 * @throws InjectionException when injector lacks a means to inject injectables.
	 */
	void initialize(World world, Map<String, Object> injectables);

	/**
	 * Inject dependencies on object. The injector delegates to {@link com.artemis.injection.FieldHandler} to resolve
	 * feiled values.
	 *
	 * @param target object which should have dependencies injected.
	 * @throws RuntimeException
	 * @see FieldHandler
	 */
	void inject(Object target) throws RuntimeException;

	/**
	 * Determins if a target object can be injected by this injector.
	 *
	 * @param target eligable for injection
	 * @return true if the Injector is capable of injecting the target object.
	 */
	boolean isInjectable(Object target);

	/**
	 * Enables the injector to be configured with a custom {@link com.artemis.injection.FieldHandler} which will
	 * be used to resolve instance values for target-fields.
	 *
	 * @param fieldHandler to use for resolving dependency values
	 * @return this Injector for chaining
	 */
	Injector setFieldHandler(FieldHandler fieldHandler);

}
