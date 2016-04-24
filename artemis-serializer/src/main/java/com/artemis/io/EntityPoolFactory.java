package com.artemis.io;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;

import java.util.Arrays;

/**
 * Maintains the pool of entities to be laoded; ensures that the
 * entity id order matches the order in the json.
 */
class EntityPoolFactory {
	private final Archetype archetype;
	private final World world;

	private IntBag pool = new IntBag();
	private int poolIndex;

	EntityPoolFactory(World world) {
		this.world = world;
		archetype = new ArchetypeBuilder().build(world);
	}

	void configureWith(int count) {
		poolIndex = 0;
		pool.setSize(0);
		pool.ensureCapacity(count);
		for (int i = 0; i < count; i++) {
			pool.add(world.create(archetype));
		}

		Arrays.sort(pool.getData(), 0, pool.size());
	}

	Entity createEntity() {
		return world.getEntity(pool.getData()[poolIndex++]);
	}
}
