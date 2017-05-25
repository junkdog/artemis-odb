package com.artemis.system.iterating;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;

public class IntOptimizedSystemAdditional extends IteratingSystem {

	public IntOptimizedSystemAdditional() {
		super(Aspect.all());

		setEnabled(true);
		begin();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (world != null)
			super.setEnabled(enabled);
	}

	@Override
	protected void begin() {
		super.begin();
	}

	@Override
	protected void process(int e) {}
}
