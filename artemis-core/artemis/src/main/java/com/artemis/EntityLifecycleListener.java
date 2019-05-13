package com.artemis;

import com.artemis.annotations.UnstableApi;

/**
 * Listener for events in the entity lifecycle.
 *
 * Use this for debugging or cases where you need to decorate every entity.
 *
 * @author junkdog
 * @author Daan van Yperen
 */
@UnstableApi
public interface EntityLifecycleListener {

    /**
     * Intercept deletion issued.
     *
     * Triggers on {@code deleteFromWorld} invocations, just before the entity
     * is scheduled for deletion. Accessing components is still allowed.
     *
     * Entity deletion is finalized at a later moment in the engine lifecycle.
     *
     * @param entityId
     */
    void onEntityDeleteIssued(int entityId);

    /**
     * Intercept entity post creation.
     *
     * @param entityId Id of created Entity.
     */
    void onEntityCreated(int entityId);

    /**
     * Intercept invalid entity exception. Happens when user requests an entity that does not exist.
     *
     * @param entityId Id of the entity that could not be found.
     */
    void onEntityNotFoundException(int entityId);
}
