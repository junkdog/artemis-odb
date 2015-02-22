package com.artemis.system;


import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Profile;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.util.SimpleProfiler;

@Profile(enabled=true, using=SimpleProfiler.class)
public class ProfiledSystem extends EntityProcessingSystem {
	public ProfiledSystem() {
		super(Aspect.all());
	}

	@Override
	protected void process(Entity e) {

	}
}
