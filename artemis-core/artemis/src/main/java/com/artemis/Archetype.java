package com.artemis;

/**
 * Provides a blueprint for new entities, offering greatly
 * improved insertion performance for systems.
 * </p>
 * Instance entities using {@link com.artemis.World#create(Archetype)}
 *
 * @see EntityEdit for a list of alternate ways to alter composition and access components.
 */
public final class Archetype {
    final EntityTransmuter.TransmuteOperation transmuter;
    public final int compositionId;
    public final String name;

    /**
     * @param transmuter    Desired composition of derived components.
     * @param compositionId uniquely identifies component composition.
     * @param name          uniquely identifies Archetype by name.
     */
    Archetype(EntityTransmuter.TransmuteOperation transmuter, int compositionId, String name) {
        this.transmuter = transmuter;
        this.compositionId = compositionId;
        this.name = name;
    }
}