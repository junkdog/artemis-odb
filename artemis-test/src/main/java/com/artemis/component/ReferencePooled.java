package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class ReferencePooled extends Component {
	public String hi = "hi";
	public boolean yup = true;
	public boolean nope = false;
	public byte byteValue = 0x10;
	public short shortValue = 20;
	public int intValue = 30;
	public long longValue = 1;
	public float floatValue = -0.222f;
	public double doubleValue = 1337;
	public double doubleValue2 = 0;
}
