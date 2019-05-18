package com.artemis;

import com.artemis.annotations.UnstableApi;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Produces internal classes.
 * <p>
 * Some provided classes are considered internal implementations and no guarantees are given,..
 * <p>
 * Overriding the internal factory is not recommended that depend greatly on implementation details to begin
 * with, like debuggers and editors.
 *
 * @author Daan van Yperen
 */
@UnstableApi
class InternalFactory {
    InternalFactory() {
        // implemented as a class to force package-locality.
    }

    ComponentManager createComponentManager(int expectedEntityCount) {
        throw new RuntimeException();
    }

    EntityManager createEntityManager(int expectedEntityCount) {
        throw new RuntimeException();
    }

    AspectSubscriptionManager createSubscriptionManager() {
        throw new RuntimeException();
    }

    BatchChangeProcessor createBatchChangeProcessor(World w) {
        throw new RuntimeException();
    }

}
