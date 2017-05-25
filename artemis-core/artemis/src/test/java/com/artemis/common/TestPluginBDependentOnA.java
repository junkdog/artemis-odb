package com.artemis.common;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.common.TestPluginA;

/**
 * @author Daan van Yperen
 */
public class TestPluginBDependentOnA implements ArtemisPlugin {
	@Override
	public void setup(WorldConfigurationBuilder b) {
		b.dependsOn(TestPluginA.class);
	}
}
