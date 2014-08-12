package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class SimpleComponent extends Component {
//	public float value, value2;
//	public float x, y;
	public int value;
//	public float x;
	
	public void set(int value) {
		this.value = value;
	}
	
	public int get() {
		return value;
	}
}
