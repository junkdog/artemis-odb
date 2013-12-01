package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;
import com.artemis.util.Vec2f;

@PackedWeaver
public class Position extends Component {
	public float x;
	public float y;
	
	@Override
	public String toString() {
		return "Position(x=" + x + ", y=" + y + ")";
	}
	
//	public float x() {
//		return x;
//	}
//	
//	public float y() {
//		return y;
//	}
//	
//	public Position x(float value) {
//		this.x = value;
//		return this;
//	}
//	
//	public Position y(float value) {
//		this.y = value;
//		return this;
//	}
//	
//	public Position set(Vec2f v) {
//		this.x = v.x();
//		this.y = v.y;
//		return this;
//	}
	
	
}
