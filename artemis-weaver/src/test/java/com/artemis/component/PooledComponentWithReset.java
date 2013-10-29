package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class PooledComponentWithReset extends Component {
	private boolean hasBeenReset;
	
	public void reset() {
		hasBeenReset = true;
	}
}