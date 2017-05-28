package com.artemis.component;

import com.artemis.PooledComponent;

public class PooledString extends PooledComponent {
	public String s;

	@Override
	protected void reset() {
		s = null;
	}
}