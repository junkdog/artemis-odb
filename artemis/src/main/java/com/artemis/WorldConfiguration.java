package com.artemis;

import com.artemis.injection.Injector;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import static com.artemis.EntityManager.NO_COMPONENTS;

public final class WorldConfiguration {
	final Bag<BaseSystem> systems = new Bag<BaseSystem>();

	protected int expectedEntityCount = 128;
	protected Map<String, Object> injectables = new HashMap<String, Object>();

	protected Injector injector;
	protected SystemInvocationStrategy invocationStrategy;

	public WorldConfiguration() {
		// reserving space for core managers
		systems.add(null); // ComponentManager
		systems.add(null); // EntityManager
		systems.add(null); // AspectSubscriptionManager
	}

	public int expectedEntityCount() {
		return expectedEntityCount;
	}

	/**
	 * Initializes array type containers with the value supplied.
	 * 
	 * @param expectedEntityCount count of expected entities.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration expectedEntityCount(int expectedEntityCount) {
		this.expectedEntityCount = expectedEntityCount;
		return this;
	}

	/**
	 * Set Injector to handle all dependency injections.
	 *
	 * @param injector Injector to handle dependency injections.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration setInjector(Injector injector) {
		if ( injector == null ) throw new NullPointerException();
		this.injector = injector;
		return this;
	}

	/**
	 * Set strategy for invoking systems on {@see World#process()}.
	 * @param invocationStrategy Strategy that will invoke systems.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration setInvocationStrategy(SystemInvocationStrategy invocationStrategy) {
		if ( invocationStrategy == null ) throw new NullPointerException();
		this.invocationStrategy = invocationStrategy;
		return this;
	}

	@Deprecated
	public int maxRebuiltIndicesPerTick() {
		return -1;
	}
	
	/**
	 * Maximum limit on how many active entity indices are rebuilt each time
	 * {@link World#process()} is invoked. An index is flagged as dirty whenever
	 * an {@link Entity} is removed or added to a system.
	 * 
	 * @param maxRebuiltIndicesPerTick 0 or more.
	 * @return This instance for chaining.
	 * @deprecated All indices are always rebuilt now. This method has no effect.
	 */
	@Deprecated
	public WorldConfiguration maxRebuiltIndicesPerTick(int maxRebuiltIndicesPerTick) {
		return this;
	}

	/**
	 * Manually register object for injection by type.
	 *
	 * Explicitly annotate to be injected fields with <code>@Wire</code>. A class level
	 * <code>@Wire</code> annotation is not enough.
	 *
	 * Since objects are injected by type, this method is limited to one object per type.
	 * Use {@link #register(String, Object)} to register multiple objects of the same type.
	 *
	 * Not required for systems.
	 *
	 * @param o object to inject.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration register(Object o) {
		return register(o.getClass().getName(), o);
	}

	/**
	 * Manually register object for injection by name.
	 *
	 * Explicitly annotate to be injected fields with <code>@Wire(name="myName")</code>. A class
	 * level <code>@Wire</code> annotation is not enough.
	 *
	 * Not required for systems.
	 *
	 * @param name unique identifier matching injection site name.
	 * @param o object to inject.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration register(String name, Object o) {
		injectables.put(name, o);
		return this;
	}

	/**
	 * Adds a system to this world that will be processed by
	 * {@link World#process()}.
	 *
	 * @param system the system to add
	 */
	public WorldConfiguration setSystem(Class<? extends BaseSystem> system) {
		return setSystem(system, false);
	}

	/**
	 * Adds a system to this world that will be processed by
	 * {@link World#process()}.
	 *
	 * @param system the system to add
	 * @return the added system
	 */
	public WorldConfiguration setSystem(Class<? extends BaseSystem> system, boolean passive) {
		try {
			return setSystem(ClassReflection.newInstance(system), passive);
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a system to this world that will be processed by
	 * {@link World#process()}.
	 *
	 * @param <T>	the system class type
	 * @param system the system to add
	 * @return the added system
	 */
	public <T extends BaseSystem> WorldConfiguration setSystem(T system) {
		return setSystem(system, false);
	}

		/**
	 * Will add a system to this world.
	 *
	 * @param <T>	 the system class type
	 * @param system  the system to add
	 * @param passive whether or not this system will be processed by
	 *				{@link World#process()}
	 * @return the added system
	 */
	public <T extends BaseSystem> WorldConfiguration setSystem(T system, boolean passive) {
		system.setPassive(passive);
		systems.add(system);

		return this;
	}

	/**
	 * Add a manager into this world.
	 * <p>
	 * It can be retrieved later. World will notify this manager of changes to
	 * entity.
	 * </p>
	 *
	 * @param <T>	 class type of the manager
	 * @param manager manager to be added
	 * @return the manager
	 */
	public final <T extends Manager> WorldConfiguration setManager(Class<T> manager) {
		try {
			return setSystem(ClassReflection.newInstance(manager));
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	void initialize(World world, Injector injector, AspectSubscriptionManager asm) {

		if ( invocationStrategy != null ) {
			world.setInvocationStrategy(invocationStrategy);
		}

		systems.set(0, world.getComponentManager());
		systems.set(1, world.getEntityManager());
		systems.set(2, asm);

		for (BaseSystem system : systems) {
			world.systems.put(system.getClass(), system);
			system.setWorld(world);
		}

		injector.initialize(world, injectables);

		initializeSystems(injector);

		asm.processComponentIdentity(NO_COMPONENTS, new BitSet());
	}

	private void initializeSystems(Injector injector) {
		for (int i = 0, s = systems.size(); i < s; i++) {
			BaseSystem system = systems.get(i);
			injector.inject(system);
			system.initialize();
		}
	}
}
