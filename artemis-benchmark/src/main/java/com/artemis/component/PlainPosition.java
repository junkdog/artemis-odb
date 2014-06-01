package com.artemis.component;

import com.artemis.Component;
import com.artemis.util.Vec2f;

public class PlainPosition extends Component
{
	public float x;
	public float y;

	public PlainPosition xy(float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public PlainPosition add(Vec2f vec)
	{
		this.x += vec.x;
		this.y += vec.y;
		return this;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}
}
