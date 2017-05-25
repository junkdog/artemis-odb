package com.artemis;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.IntDeque;

import com.artemis.utils.BitVector;

import static com.artemis.Aspect.all;

/**
 * Manages entity instances.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
@SkipWire
public class EntityManager extends BaseSystem {
    protected final BitVector active = new BitVector();
    private final BitVector recycled = new BitVector();
    private final IntDeque limbo = new IntDeque();
    private int nextId;
    private Bag<BitVector> entityBitVectors = new Bag<BitVector>(BitVector.class);

    /**
     * Creates a new EntityManager Instance.
     */
    protected EntityManager(int initialContainerSize) {
        registerEntityStore(recycled);
        registerEntityStore(active);
    }

    @Override
    protected void processSystem() {
    }

    /**
     * Create a new entity.
     *
     * @return a new entity id
     */
    protected int create() {
        if (limbo.isEmpty()) {
            final int id = nextId++;
            active.set(id);
            return id;
        } else {
            int id = limbo.popFirst();
            recycled.unsafeClear(id);
            active.set(id);
            return id;
        }
    }


    void clean(IntBag pendingDeletion) {
        int[] ids = pendingDeletion.getData();
        for (int i = 0, s = pendingDeletion.size(); s > i; i++) {
            int id = ids[i];
            // usually never happens but:
            // this happens when an entity is deleted before
            // it is added to the world, ie; created and deleted
            // before World#process has been called
            if (!recycled.unsafeGet(id)) {
                free(id);
            }
        }
    }

    /**
     * Check if this entity is active.
     * <p>
     * Active means the entity is being actively processed.
     * </p>
     *
     * @param entityId the entities id
     * @return true if active, false if not
     */
    public boolean isActive(int entityId) {
        return !recycled.unsafeGet(entityId);
    }

    public void registerEntityStore(BitVector bv) {
        bv.ensureCapacity(active.length());
        entityBitVectors.add(bv);
    }

    /**
     * <p>If all entities have been deleted, resets the entity cache - with next entity
     * entity receiving id <code>0</code>. There mustn't be any active entities in
     * the world for this method to work. This method does nothing if it fails.</p>
     * <p>
     * <p>For the reset to take effect, a new {@link World#process()} must initiate.</p>
     *
     * @return true if entity id was successfully reset.
     */
    public boolean reset() {
        int count = world.getAspectSubscriptionManager()
                .get(all())
                .getActiveEntityIds()
                .cardinality();
        if (count > 0) {
            return false;
        } else {
            actuallyReset();
            return true;
        }
    }

    protected void actuallyReset() {
        limbo.clear();
        recycled.clear();
        active.clear();
        nextId = 0;
    }

    protected void growEntityStores() {
        int newSize = 2 * active.length();
        ComponentManager cm = world.getComponentManager();
        cm.ensureCapacity(newSize);

        for (int i = 0, s = entityBitVectors.size(); s > i; i++) {
            entityBitVectors.get(i).ensureCapacity(newSize);
        }
    }

    private void free(int entityId) {
        limbo.add(entityId);
        recycled.unsafeSet(entityId);
    }
}
