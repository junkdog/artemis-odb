package com.artemis.system;

import java.util.Random;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.ComponentTypeBenchmark;
import com.artemis.systems.VoidEntitySystem;

public final class EntityDeleterSystem extends VoidEntitySystem {

	int[] ids = new int[ComponentTypeBenchmark.ENTITY_COUNT];
	
	int counter;
	int index;

	private Class<? extends Component> c1;
	private Class<? extends Component> c2;

	public EntityDeleterSystem(long seed, Class<? extends Component> c1, Class<? extends Component> c2) {
		this.c1 = c1;
		this.c2 = c2;
		Random rng = new Random(seed);
		for (int i = 0; ids.length > i; i++)
			ids[i] = (int)(rng.nextFloat() * ComponentTypeBenchmark.ENTITY_COUNT);
	}
	
	@Override
	protected void initialize() {
		for (int i = 0; ComponentTypeBenchmark.ENTITY_COUNT > i; i++)
			createEntity();
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
	
	protected final void createEntity() {
		Entity e = world.createEntity();
		e.createComponent(c1);
		e.createComponent(c2);
		e.addToWorld();
	}
}
