package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.systems.EntityProcessingSystem;

public final class PoorFellowSystem extends EntityProcessingSystem {

	public PoorFellowSystem(Aspect aspect) {
		super(Aspect.all());
	}

	@Override
	protected void process(int e) {
		System.out.println("hello!");
	}
}
