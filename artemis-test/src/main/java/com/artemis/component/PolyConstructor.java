package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class PolyConstructor extends Component {
	public float diameter;

	public PolyConstructor() {
		
	}
	
	public PolyConstructor(float diameter) {
		this.diameter = diameter;
	}
}
