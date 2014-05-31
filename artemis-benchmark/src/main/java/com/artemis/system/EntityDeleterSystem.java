package com.artemis.system;

import java.util.Random;

import com.artemis.Entity;
import com.artemis.ComponentTypeBenchmark;
import com.artemis.systems.VoidEntitySystem;

public abstract class EntityDeleterSystem extends VoidEntitySystem {

	int[] ids = new int[ComponentTypeBenchmark.ENTITY_COUNT];
	
	int counter;
	int index;

	public EntityDeleterSystem(long seed) {
		Random rng = new Random(seed);
		for (int i = 0; ids.length > i; i++)
			ids[i] = (int)(rng.nextFloat() * ComponentTypeBenchmark.ENTITY_COUNT);
	}
	
	@Override
	protected void begin() {
		counter++;
	}

	@Override
	protected void processSystem() {
		if (counter == 100) {
			Entity e = world.getEntity(ids[index++]);
			world.deleteEntity(e);
			index = index % ComponentTypeBenchmark.ENTITY_COUNT;
			counter = 0;
		} else if (counter == 1) { // need to wait one round to reclaim entities
			createEntity();
		}
	}
	
	protected abstract void createEntity();
}
