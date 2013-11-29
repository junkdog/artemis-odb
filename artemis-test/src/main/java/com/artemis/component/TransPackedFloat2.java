package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;
import com.artemis.util.Vec2f;

@PackedWeaver
public class TransPackedFloat2 extends Component {
	public float x;
	public float y;
	
//	public float x() {
//		return x;
//	}
//	
//	public float y() {
//		return y;
//	}
	
//	public TransPackedFloat x(float value) {
//		this.x = value;
//		return this;
//	}
//	
//	public TransPackedFloat y(float value) {
//		this.y = value;
//		return this;
//	}
//	
//	public TransPackedFloat addX(float value) {
//		this.x += value;
//		return this;
//	}
	
	public TransPackedFloat2 set(Vec2f v) {
		this.x = v.x();
		this.y = v.y;
		return this;
	}
	
	public TransPackedFloat2 add(Vec2f v) {
		this.x += v.x();
		this.y += v.y;
		return this;
	}

	public TransPackedFloat2 addY(float value) {
		this.y += value;
		return this;
	}

	public void init(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
