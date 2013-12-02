package com.artemis.component;

import com.artemis.util.Vec2f;


public class Access {

	private Position position;
//	Vec2f vec = new Vec2f(2, 3);
	
	public Access(Position position) {
		this.position = position;
	}
	
	public float setAndGetF(float value) {
		position.y = value;
		return position.y;
	}
	
	public float incAndGetF(float value) {
		position.x = 5f;
		position.x += value;
		
		return position.x;
	}
	
	public float mulAndGetF(float value1, float value2) {
		position.x += value1;
		position.x *= value2;
		
		return position.x;
	}
	
//	public float setAndGetF(float value) {
//		vec.y = value;
//		return vec.y;
//	}
//	
//	public float setAndGetM(float value) {
//		vec.y(value);
//		return vec.y();
//	}
	
//	public void getM() {
//		float value = 5;
//		value = component.getX();
//	}
//	
//	
//	public void setM() {
//		float value = 5;
//		component.setX(value);
//	}
//	
//	public void incF() {
//		float value = 5;
//		component.x += value;
//	}
//	
//	public void incM() {
//		float value = 5;
//		component.setX(component.getX() + value);
//	}
}
