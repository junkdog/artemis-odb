package com.artemis.system;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;

public final class IteratingPoorFellowSystem extends IteratingSystem {

	public IteratingPoorFellowSystem(Aspect aspect) {
		super(Aspect.all());
	}

	@Override
	protected void process(int e) {
		System.out.println("hello!");
	}
}
