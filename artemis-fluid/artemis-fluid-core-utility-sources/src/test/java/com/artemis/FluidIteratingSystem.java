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

    /**
     * Return all entities matching aspect.
     * Calling the aspect builder is relatively expensive but should be fine outside tight loops.
     */
    protected EBag allEntitiesMatching(Aspect.Builder scope) {
        return new EBag(world.getAspectSubscriptionManager().get(scope).getEntities());
    }

    /**
     * Return all entities matching a class.
     * Calling the aspect builder is relatively expensive but should be fine outside tight loops.
     */
    protected EBag allEntitiesWith(Class<? extends Component> scope) {
        return new EBag(world.getAspectSubscriptionManager().get(Aspect.all(scope)).getEntities());
    }
}
