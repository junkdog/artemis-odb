package com.artemis.system;

import java.util.Random;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.PackedBenchmark;
import com.artemis.systems.VoidEntitySystem;

public class EntityDeleterSystem extends VoidEntitySystem {

	int[] ids = new int[PackedBenchmark.ENTITY_COUNT];
	
	int counter;
	int index;

	private Class<? extends Component> componentType;
	
	public EntityDeleterSystem(long seed, Class<? extends Component> componentType) {
		this.componentType = componentType;
		Random rng = new Random(seed);
		for (int i = 0; ids.length > i; i++)
			ids[i] = (int)(rng.nextFloat() * PackedBenchmark.ENTITY_COUNT);
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
			index = index % PackedBenchmark.ENTITY_COUNT;
			counter = 0;
		} else if (counter == 1) { // need to wait one round to reclaim entities
			PackedBenchmark.createEntity(world, componentType);
		}
	}

}
