package com.artemis.common;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;

/**
 * @author Daan van Yperen
 */
public class TestPluginCDependentOnA implements ArtemisPlugin {
	@Override
	public void setup(WorldConfigurationBuilder b) {
		b.dependsOn(TestPluginA.class);
	}
}
