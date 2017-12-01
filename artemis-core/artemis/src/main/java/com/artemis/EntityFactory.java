package com.artemis;

/**
 * @author Daan van Yperen
 */
public interface EntityFactory<W extends World, T extends Entity> {
    T instance(W world, int entityId);
}
