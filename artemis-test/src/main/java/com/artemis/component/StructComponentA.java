package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class StructComponentA extends Component {
	public float x, y, z;
	public short something;
	public boolean flag;
	
	public StructComponentA setXyz(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}

	@Override
	public String toString() {
		return "StructComponentA [x=" + x + ", y=" + y + ", z=" + z + ", something=" + something + ", flag=" + flag +
			"]";
	}
	
	protected void enscureCapacity(int id) {
//		if (($data.capacity() - $_SIZE_OF) <= $stride) $grow();
	}
}
