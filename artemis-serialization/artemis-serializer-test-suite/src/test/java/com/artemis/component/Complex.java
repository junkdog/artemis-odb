package com.artemis.component;

import com.artemis.Component;
import com.artemis.utils.Vector2;

public class Complex extends Component {
	public Vector2 pos = new Vector2(0, 0);
	public Vector2 vel = new Vector2(0, 0);

	public void pos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}
	
	public void vel(float x, float y) {
		vel.x = x;
		vel.y = y;
	}
}
