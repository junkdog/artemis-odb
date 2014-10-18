package com.artemis.component;

import com.artemis.Component;
import com.artemis.util.Vec2f;

public class Complex extends Component {
	public Vec2f pos = new Vec2f(0, 0);
	public Vec2f vel = new Vec2f(0, 0);;
	
	public void pos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}
	
	public void vel(float x, float y) {
		vel.x = x;
		vel.y = y;
	}
}
