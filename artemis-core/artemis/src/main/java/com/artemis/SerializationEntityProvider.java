package com.artemis;

/**
 * Implementing classes support serialization.
 *
 * @author Daan van Yperen
 */
public interface SerializationEntityProvider<T extends Entity> {
    T getEntity( int entityId );
}
