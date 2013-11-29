package com.artemis.component;

import com.artemis.annotations.PackedWeaver;


@PackedWeaver
public class PackedFields {
	public float x;
	public float y;
	
	public void x(float x) {
		this.x = x;
	}
	
	public float x() {
		return x;
	}
}
