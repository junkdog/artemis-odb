package com.artemis.injection;

/**
 * Enum used by {@link ClassType} to indicate if a class or field is annotated with {@link com.artemis.annotations.Wire}
 * or {@link com.artemis.annotations.Mapper}.
 * @author Snorre E. Brekke
 */
public enum WireType {
    /**
     * Indicates that a class is annotated with {@link com.artemis.annotations.Wire}
     */
    WIRE,
    /**
     * Indicates that a class is annotated with {@link com.artemis.annotations.Mapper}
     */
    MAPPER,
    /**
     * Indicates that a class is not annotated with anything relevant to the artemis-world.
     */
    IGNORED
}
