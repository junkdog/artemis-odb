package com.artemis;

import java.util.HashMap;
import java.util.Map;

public final class WorldConfiguration {
	private int expectedEntityCount = 128;
	Map<String, Object> injectables = new HashMap<String, Object>();
	
	public int expectedEntityCount() {
		return expectedEntityCount;
	}
	/**
	 * Initializes array type containers with the value supplied.
	 * 
	 * @param expectedEntityCount count of expected entities.
	 * @return This instance for chaining.
	 */
	@Deprecated
	public WorldConfiguration expectedEntityCount(int expectedEntityCount) {
		this.expectedEntityCount = expectedEntityCount;
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
	 * Not required for systems and managers.
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
	 * Not required for systems and managers.
	 *
	 * @param name unique identifier matching injection site name.
	 * @param o object to inject.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration register(String name, Object o) {
		injectables.put(name, o);
		return this;
	}
}
