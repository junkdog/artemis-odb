package com.artemis;

import java.util.BitSet;

public final class EntityTransmuterFactory {
	private final ComponentTypeFactory types;
	private final BitSet additions;
	private final BitSet removals;
	private World world;

	public EntityTransmuterFactory(World world) {
		this.world = world;
		types = world.getComponentManager().typeFactory;
		additions = new BitSet();
		removals = new BitSet();
	}

	public EntityTransmuterFactory add(Class<? extends Component> component) {
		int index = types.getIndexFor(component);
		additions.set(index, true);
		removals.set(index, false);
		return this;
	}

	public EntityTransmuterFactory remove(Class<? extends Component> component) {
		int index = types.getIndexFor(component);
		additions.set(index, false);
		removals.set(index, true);
		return this;
	}

	public EntityTransmuter build() {
		return new EntityTransmuter(world, additions, removals);
	}
}