package com.artemis;

public class FakeEntityFactory {
	private FakeEntityFactory() {}

	public static Entity create(World world, int entityId) {
		return new Entity(world, entityId);
	}
}
