package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class TransPackedInt extends Component {
	private int x;
	private int y;
	private int z;
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public int z() {
		return z;
	}
	
	public TransPackedInt x(int value) {
		this.x = value;
		return this;
	}
	
	public TransPackedInt y(int value) {
		this.y = value;
		return this;
	}
	
	public TransPackedInt z(int value) {
		this.z = value;
		return this;
	}
	
	public TransPackedInt subX(int value) {
		this.x -= value;
		return this;
	}

	public TransPackedInt divY(int value) {
		this.y /= value;
		return this;
	}
	
	public TransPackedInt mulZ(int value) {
		this.z *= value;
		return this;
	}
}
