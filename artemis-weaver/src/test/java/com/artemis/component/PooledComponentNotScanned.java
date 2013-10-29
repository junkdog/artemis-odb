package com.artemis.component;

import com.artemis.PooledComponent;

public class PooledComponentNotScanned extends PooledComponent {
	private boolean hasBeenReset;
	
	@Override
	public void reset() {
		hasBeenReset = true;
	}
}