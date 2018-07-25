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
	 * Artemis will consider abstract plugin dependencies fulfilled when a concrete subclass has been registered
	 * beforehand.
	 *
	 * To create a common API with different implementations (like logging-api and logging-libgdx) create a superclass
	 * plugin that is abstract, and one subclass for each implementation.
	 *
	 * @param b builder to register your dependencies with.
	 */
	void setup(WorldConfigurationBuilder b);
}
