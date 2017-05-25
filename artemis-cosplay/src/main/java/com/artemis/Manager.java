package com.artemis;

import com.artemis.utils.IntBag;

import static com.artemis.Aspect.all;


/**
 * A manager for handling entities in the world.
 * <p>
 * In odb Manager has been absorbed into the {@link BaseSystem} hierarchy.
 * While Manager is still available we recommend implementing new
 * managers using IteratingSystem, {@link BaseEntitySystem} with
 * {@link Aspect#all()}, or {@link BaseSystem} depending on your needs.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public abstract class Manager<T extends Entity> extends CosplayBaseSystem<T> {
    private int methodFlags;

    /**
     * Called when entity gets added to world.
     */
    public void added(T e) {
        throw new RuntimeException("I shouldn't be here...");
    }

    /**
     * Called when entity gets deleted from world.
     */
    public void deleted(T e) {
        throw new RuntimeException("... if it weren't for the tests.");
    }

    /**
     * Set the world this system works on.
     *
     * @param world the world to set
     */
    @Override
    protected void setWorld(World world) {
        super.setWorld(world);
        if (implementsObserver("added"))
            methodFlags |= FLAG_INSERTED;
        if (implementsObserver("deleted"))
            methodFlags |= FLAG_REMOVED;
    }

    @Override
    protected void initialize() {
        /** Hack to register manager to right subscription */
        world.getAspectSubscriptionManager()
                .get(all())
                .addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
                    @Override
                    public void inserted(IntBag entities) {
                        added(entities);
                    }

                    @Override
                    public void removed(IntBag entities) {
                        deleted(entities);
                    }
                });
    }

    private void added(IntBag entities) {
        // performance hack, skip if manager lacks implementation of inserted.
        if ((methodFlags & FLAG_INSERTED) == 0)
            return;

        final int[] ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            added(worldTyped.getEntity(ids[i]));
        }
    }

    private void deleted(IntBag entities) {
        // performance hack, skip if manager lacks implementation of removed.
        if ((methodFlags & FLAG_REMOVED) == 0)
            return;

        final int[] ids = entities.getData();
        for (int i = 0, s = entities.size(); s > i; i++) {
            deleted(worldTyped.getEntity(ids[i]));
        }
    }

    /**
     * Managers are not interested in processing.
     */
    @Override
    protected final void processSystem() {
    }
}
