package com.artemis.utils;

import com.artemis.EntitySystem;
import com.artemis.World;
import com.artemis.annotations.Profile;


/**
 * @see Profile
 */
public interface ArtemisProfiler {
	void start();
	void stop();
	void initialize(EntitySystem owner, World world);
}