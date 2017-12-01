package com.artemis.component;

import com.artemis.Component;
import com.artemis.utils.Vector2;

public class Position extends Component {
	public Vector2 xy = new Vector2();

	public Position xy(float x, float y)
	{
		xy.x = x;
		xy.y = y;
		return this;
	}

	public Position add(Vector2 vec)
	{
		xy.x += vec.x;
		xy.y += vec.y;
		return this;
	}

	@Override
	public String toString() {
		return "Position [x=" + xy.x + ", y=" + xy.y + "]";
	}
}
