package com.artemis.io;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.esotericsoftware.jsonbeans.JsonValue;

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

	void configureWith(JsonValue jsonData) {
		assert (pool.isEmpty());

		int count = countChildren(jsonData.get("entities"));
		preallocateEntities(count);
	}

	Entity createEntity() {
		return world.getEntity(pool.get(poolIndex++));
	}

	private int countChildren(JsonValue jsonData) {
		if (jsonData == null || jsonData.child == null)
			return 0;

		JsonValue entity = jsonData.child;
		int count = 0;
		while (entity != null) {
			count++;
			entity = entity.next;
		}
		return count;
	}

	private void preallocateEntities(int count) {
		pool.setSize(0);
		pool.ensureCapacity(count);
		for (int i = 0; i < count; i++) {
			pool.add(world.create(archetype));
		}

		Arrays.sort(pool.getData(), 0, pool.size());
	}
}
