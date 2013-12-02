package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;
import com.artemis.util.Vec2f;

@PackedWeaver
public class PackedFieldComponent extends Component {
	public float x;
	public float y;
	
	@Override
	public String toString() {
		return "Position(x=" + x + ", y=" + y + ")";
	}

	public PackedFieldComponent set(Vec2f v) {
		this.x = v.x();
		this.y = v.y;
		return this;
	}
}
