package com.artemis.component;

import com.artemis.Entity;
import com.artemis.PackedComponent;
import com.artemis.util.Vec2f;

@SuppressWarnings("unused")
public class TransPackedFloatReferencel extends PackedComponent {

	private int $offset;
	private static final int $_SIZE_OF = 2;
	private static float[] $data = new float[64];
	

	@Override
	protected PackedComponent forEntity(Entity e) {
		this.$offset = $_SIZE_OF * e.getId();
		if (($data.length - 1) <= $offset) $grow();
		return this;
	}

	@Override
	protected void reset() {
		$data[$offset + 0] = 0;
		$data[$offset + 1] = 0;
	}
	
	private static void $grow()
	{
		float[] old = $data;
		$data = new float[(old.length * 2)];
		System.arraycopy(old, 0, $data, 0, old.length);
	}
	
	public float x() {
		return $data[$offset + 0];
	}
	
	public float y() {
		return $data[$offset + 1];
	}
	
	public void x(float value) {
		$data[$offset + 0] = value;
		return this;
	}
	
	public void y(float value) {
		$data[$offset + 1] = value;
		return this;
	}
	
	
	public void set(Vec2f v) {
		$data[$offset + 0] = v.x();
		$data[$offset + 1] = v.y;
		return this;
	}
}
