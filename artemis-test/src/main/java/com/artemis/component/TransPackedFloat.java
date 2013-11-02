package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class TransPackedFloat extends Component {
	private float x;
	private float y;
	
	public float x() {
		return x;
	}
	
	public float y() {
		return y;
	}
	
	public TransPackedFloat x(float value) {
		this.x = value;
		return this;
	}
	
	public TransPackedFloat addX(float value) {
		this.x += value;
		return this;
	}
	
	public TransPackedFloat y(float value) {
		this.y = value;
		return this;
	}
	
	public TransPackedFloat add6(float value) {
		this.y += value;
		return this;
	}
}
