package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class ComponentToWeave extends Component {
	private boolean hasBeenReset;
}