package com.artemis.component;

public class Access {

	private PackedFieldComponent position;
	
	public Access(PackedFieldComponent position) {
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
}
