package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;
import com.artemis.util.Vec2f;

//@PackedWeaver
public class Excample extends Component {
	public float x;
	public float y;
	
	public float getX() {
		return x;
	}
	
	public float y() {
		return y;
	}
	
	public void setX(float value) {
		this.x = value;
	}
	
	public Excample setY(float value) {
		this.y = value;
		return this;
	}
	
	public Excample set(Vec2f v) {
		this.x = v.x();
		this.y = v.y;
		return this;
	}
}
