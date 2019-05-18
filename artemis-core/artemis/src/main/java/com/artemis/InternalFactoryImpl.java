package com.artemis;

/**
 * Default implementation of the internal factory.
 *
 * Overriding the internal factory is not recommended that depend greatly on implementation details to begin
 * with, like debuggers and editors.
 *
 * @author Daan van Yperen
 * @see InternalFactory
 */
class InternalFactoryImpl extends InternalFactory {
    @Override
    ComponentManager createComponentManager(int expectedEntityCount) {
        return new ComponentManager(expectedEntityCount);
    }

    @Override
    EntityManager createEntityManager(int expectedEntityCount) {
        return new EntityManager(expectedEntityCount);
    }

    @Override
    AspectSubscriptionManager createSubscriptionManager() {
        return new AspectSubscriptionManager();
    }

    @Override
    BatchChangeProcessor createBatchChangeProcessor(World w) {
        return new BatchChangeProcessor(w);
    }
}
