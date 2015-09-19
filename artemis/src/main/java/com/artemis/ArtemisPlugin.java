package com.artemis;

/**
 * Plugin for artemis-odb.
 *
 * @author Daan van Yperen
 */
public interface ArtemisPlugin {

	/**
	 * Register your plugin.
	 *
	 * Set up all your dependencies here.
	 * - systems
	 * - field resolvers
	 * - other plugins
	 *
	 * Always prefer to use {@link WorldConfigurationBuilder#dependsOn} as it can handle repeated dependencies,
	 * as opposed to {@link WorldConfigurationBuilder#with}, which will throw an exception upon attempting to
	 * add a pre-existing class.
	 *
	 * @param b builder to register your dependencies with.
	 */
	void setup(WorldConfigurationBuilder b);
}
