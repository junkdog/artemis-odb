package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PackedWeaver;

@PackedWeaver
public class TransPackedLong extends Component {
	private long x;
	private long y;
	private long z;
	
	public long x() {
		return x;
	}
	
	public long y() {
		return y;
	}
	
	public long z() {
		return z;
	}
	
	public TransPackedLong init(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}
	
	public TransPackedLong x(long value) {
		this.x = value;
		return this;
	}
	
	public TransPackedLong y(long value) {
		this.y = value;
		return this;
	}
	
	public TransPackedLong z(long value) {
		this.z = value;
		return this;
	}
	
	public TransPackedLong subX(long value) {
		this.x -= value;
		return this;
	}

	public TransPackedLong divY(long value) {
		this.y /= value;
		return this;
	}
	
	public TransPackedLong mulZ(long value) {
		this.z *= value;
		return this;
	}
}
