package com.artemis;

/**
 * Provides a blueprint for new entities, offering greatly
 * improved insertion performance for systems and managers.
 * </p>
 * Instance entities using {@link com.artemis.World#createEntity(Archetype)}
 */
public final class Archetype {
	final ComponentType[] types;
	final short compositionId;

	/**
	 * @param types Desired composition of derived components.
	 * @param compositionId uniquely identifies component composition.
	 */
	public Archetype(ComponentType[] types, int compositionId) {
		this.types = types;
		this.compositionId = (short) compositionId;
	}
}
