package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;


@PooledWeaver(forceWeaving=true) @SuppressWarnings("unused")
public class PooledAllFields extends Component {
	private boolean _boolean = true;
	private char _char = 'a';
	private short _short = 1;
	private int _int = 1;
	private long _long = 1;
	private float _float = 1;
	private double _double = 1;
	private String _string = "hej";
//	
//	@Override
//	protected void reset() {
//		_boolean = false;
//		_char = 0;
//		_short = 0;
//		_int = 0;
//		_long = 0;
//		_float = 0;
//		_double = 0;
//		_string = null;
//	}
}
