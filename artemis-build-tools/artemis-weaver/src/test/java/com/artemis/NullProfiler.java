package com.artemis;

import com.artemis.utils.ArtemisProfiler;

public class NullProfiler implements ArtemisProfiler {
	@Override
	public void start() {}

	@Override
	public void stop() {}

	@Override
	public void initialize(BaseSystem owner, World world) {}

}
