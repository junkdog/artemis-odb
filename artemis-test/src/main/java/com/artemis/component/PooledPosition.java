package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class PooledPosition extends Component {
	public float x;
	public float y;

	public PooledPosition xy(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}
}
