package com.artemis;

/**
 * @author Daan van Yperen
 */
public interface SerializationEntityProvider<T extends Entity> {
    T getEntity( int entityId );
}
