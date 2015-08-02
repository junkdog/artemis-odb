package com.artemis.component;

import com.artemis.PooledComponent;

public class CountingPooledComponent extends PooledComponent {
	public static int instanceCounter;
	public final int count;

	public CountingPooledComponent() {
		count = instanceCounter++;
	}

	@Override
	public void reset() {}

	@Override
	public String toString() {
		return "CountingPooledComponent{" +
				"count=" + count +
				'}';
	}
}
