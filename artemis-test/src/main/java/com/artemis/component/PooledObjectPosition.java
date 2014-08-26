package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.artemis.util.Vec2f;

@PooledWeaver
public class PooledObjectPosition extends Component {
	public Vec2f vec2 = new Vec2f(0, 0);

	public PooledObjectPosition xy(float x, float y) {
		vec2.x = x;
		vec2.y = y;
		return this;
	}

	@Override
	public String toString() {
		return "Position [x=" + vec2.x + ", y=" + vec2.y + "]";
	}
}
