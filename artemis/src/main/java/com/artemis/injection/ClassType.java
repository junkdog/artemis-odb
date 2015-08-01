package com.artemis.injection;

/**
 * Enum used to cache class type according to their usage in Artemis.
 * @author Snorre E. Brekke
 */
public enum ClassType {
    /**
     * Used for (sub)classes of {@link com.artemis.ComponentMapper}
     */
    MAPPER,
    /**
     * Used for (sub)classes of {@link com.artemis.BaseSystem}
     */
    SYSTEM,
    /**
     * Used for (sub)classes of {@link com.artemis.Manager}
     */
    MANAGER,
    /**
     * Used for (sub)classes of {@link com.artemis.EntityFactory}
     */
    FACTORY,
    /**
     * Used for everything else.
     */
    CUSTOM
}
