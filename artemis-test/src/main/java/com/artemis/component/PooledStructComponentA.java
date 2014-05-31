package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver // class is only used by benchmark...
public class PooledStructComponentA extends Component {
	public float x, y, z;
	public short something;
	public boolean flag;
	
	public PooledStructComponentA setXyz(float x, float y, float z) {
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
}
