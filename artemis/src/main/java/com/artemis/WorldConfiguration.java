package com.artemis;

public class WorldConfiguration {
	private int expectedEntityCount = 128;
	private int maxRebuiltIndicesPerTick = 1;
	
	public int expectedEntityCount() {
		return expectedEntityCount;
	}
	/**
	 * Initializes array type containers with the value supplied.
	 * 
	 * @param expectedEntityCount
	 * @return This instance for chaining.
	 */
	public WorldConfiguration expectedEntityCount(int expectedEntityCount) {
		this.expectedEntityCount = expectedEntityCount;
		return this;
	}
	public int maxRebuiltIndicesPerTick() {
		return maxRebuiltIndicesPerTick;
	}
	/**
	 * Maximum limit on how many active entity indices are rebuilt each time
	 * {@link World#process()} is invoked. An index is flagged as dirty whenever
	 * an {@link Entity} is removed or added to a system.
	 * 
	 * @param maxRebuiltIndicesPerTick 0 or more.
	 * @return This instance for chaining.
	 */
	public WorldConfiguration maxRebuiltIndicesPerTick(int maxRebuiltIndicesPerTick) {
		this.maxRebuiltIndicesPerTick = Math.max(0, maxRebuiltIndicesPerTick);
		return this;
	}
}
