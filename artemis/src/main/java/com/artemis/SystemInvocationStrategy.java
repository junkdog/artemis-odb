package com.artemis;

import com.artemis.utils.Bag;

public abstract class SystemInvocationStrategy {

	protected World world;

	protected final void setWorld(World world) {
		this.world = world;
	}

	protected final void updateEntityStates() {
		world.updateEntityStates();
	}

	protected abstract void process(Bag<BaseSystem> systems);
}
