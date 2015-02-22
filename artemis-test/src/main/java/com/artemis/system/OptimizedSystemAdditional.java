package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

public class OptimizedSystemAdditional extends EntityProcessingSystem {

	public OptimizedSystemAdditional() {
		super(Aspect.all());

		setEnabled(true);
		begin();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	protected void begin() {
		super.begin();
	}

	@Override
	protected void process(Entity e) {}
}
