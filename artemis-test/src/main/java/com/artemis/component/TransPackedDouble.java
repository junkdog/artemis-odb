package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class TransPackedDouble extends Component {
	private double x;
	
	public double x() {
		return x;
	}
	
	public TransPackedDouble x(double value) {
		this.x = value;
		return this;
	}
}
