package com.artemis.utils;

public class Vector2 {
	public float x, y;

	public Vector2() {}
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float x() {
		return x;
	}

	public void x(float x) {
		this.x = x;
	}

	public float y() {
		return y;
	}

	public void y(float y) {
		this.y = y;
	}
}
