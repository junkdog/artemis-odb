package com.artemis;

/**
 * Provides a blueprint for new entities, offering greatly
 * improved insertion performance for systems.
 * </p>
 * Instance entities using {@link com.artemis.World#create(Archetype)}
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public final class Archetype {
	final ComponentType[] types;
	final int compositionId;

	/**
	 * @param types Desired composition of derived components.
	 * @param compositionId uniquely identifies component composition.
	 */
	Archetype(ComponentType[] types, int compositionId) {
		this.types = types;
		this.compositionId = compositionId;
	}
}