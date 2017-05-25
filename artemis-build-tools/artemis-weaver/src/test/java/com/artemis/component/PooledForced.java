package com.artemis.component;

import com.artemis.annotations.PooledWeaver;


@PooledWeaver(forceWeaving=true)
public class PooledForced {
	public float x;
	public float y;
}
