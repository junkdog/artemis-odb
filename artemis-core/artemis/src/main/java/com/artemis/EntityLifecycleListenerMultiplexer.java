package com.artemis;

import com.artemis.annotations.UnstableApi;

/**
 * Multiplexing implementation of EntityLifecycleListener.
 *
 * @author Daan van Yperen
 */
@UnstableApi
class EntityLifecycleListenerMultiplexer implements EntityLifecycleListener {
    private final EntityLifecycleListener[] listeners;

    /**
     * @param listeners Lists to broadcast to. Triggered in order.
     */
    public EntityLifecycleListenerMultiplexer(EntityLifecycleListener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public void onEntityDeleteIssued(int entityId) {
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].onEntityDeleteIssued(entityId);
        }
    }

    @Override
    public void onEntityCreated(int entityId) {
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].onEntityCreated(entityId);
        }
    }

    @Override
    public void onEntityNotFoundException(int entityId) {
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].onEntityNotFoundException(entityId);
        }
    }
}
