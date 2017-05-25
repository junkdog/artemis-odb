package com.artemis.system.iterating;

import com.artemis.annotations.PreserveProcessVisiblity;
import com.artemis.systems.IteratingSystem;

@PreserveProcessVisiblity
public class IntOptimizedSystemSafe extends IteratingSystem {

	public IntOptimizedSystemSafe() {
		super(null);
	}

	@Override
	protected void process(int e) {}

}
