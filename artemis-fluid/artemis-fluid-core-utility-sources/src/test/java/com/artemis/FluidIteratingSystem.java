package com.artemis;

import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;

/**
 * Iterates over {@link EntitySubscription} member entities
 * using fluid entities.
 * <p>
 * Use this when you need to process entities matching an {@link Aspect}.
 *
 * Provides some convenience methods to resolve entities.
 *
 * @author Daan van Yperen
 */
public abstract class FluidIteratingSystem extends IteratingSystem {

    public FluidIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    public FluidIteratingSystem() {
    }

    @Override
    protected void process(int id) {
        process(E.E(id));
    }

    protected abstract void process(E e);
}
