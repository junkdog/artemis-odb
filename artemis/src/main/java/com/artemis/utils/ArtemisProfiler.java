package com.artemis.utils;

import com.artemis.annotations.Profile;


/**
 * @see Profile
 */
public interface ArtemisProfiler {
	void start();
	void stop();
	void setTag(Object tag);
}