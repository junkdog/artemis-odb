package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;


/**
 * The most raw entity system.
 * <p>
 * It should not typically be used, but you can create your own entity system
 * handling by extending this.
 * </p>
 *
 * @author Arni Arent
 */
public abstract class EntitySystem implements EntityObserver {

	/** The world this system belongs to. */
	protected World world;
	
	/* 
	 * actives = only contains entities, typically sorted ASC by entity.id 
	 * activesIsDirty = indicates that actives isn't sorted; needs rebuilding 
	 */
//	private final WildBag<Entity> actives;
	private IntBag actives;
	private final BitSet activeIds;
	private boolean activesIsDirty;
	
	/** If the system is passive or not. */
	private boolean passive;
	/** If the system is enabled or not. */
	private boolean enabled;
	/** If the system is interested in no entities at all. */
	private boolean dummy;
	private Aspect aspect;
	private final BitSet aspectCache = new BitSet();

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public EntitySystem(Aspect aspect) {
		this.aspect = aspect;
		activeIds = new BitSet();
		actives = new IntBag();
		
		enabled = true;
	}


	/**
	 * Called before processing of entities begins.
	 * <p>
	 * <b>Nota Bene:</b> Any entities created in this method
	 * won't become active until the next system starts processing
	 * or when a new processing rounds beings, whichever comes first.
	 * </p>
	 */
	protected void begin() {}

	/**
	 * Process all entities this system is interested in.
	 */
	public final void process() {
		if(enabled && checkProcessing()) {
			begin();
			
			if (activesIsDirty && world.isRebuildingIndexAllowed())
				rebuildCompressedActives();
			
			processEntities(actives);

			end();
		}
	}
	
	private void rebuildCompressedActives() {
		
		BitSet bs = activeIds;
		int size = bs.cardinality();
		actives.setSize(size);
		actives.ensureCapacity(size);
		EntityManager em = world.getEntityManager();
		int[] activesArray = actives.getData();
		for (int i = bs.nextSetBit(0), index = 0; i >= 0; i = bs.nextSetBit(i + 1)) {
			activesArray[index++] = i;
		}
		
		activesIsDirty = false;
		world.rebuiltIndices++;
	}


	/**
	 * Called after the processing of entities ends.
	 */
	protected void end() {}
	
	/**
	 * Any implementing entity system must implement this method and the logic
	 * to process the given entities of the system.
	 * 
	 * @param entities
	 *			the entities this system contains.
	 */
	protected abstract void processEntities(IntBag entities);
	
	/**
	 * Check if the system should be processed.
	 *
	 * @return true if the system should be processed, false if not.
	 */
	@SuppressWarnings("static-method")
	protected boolean checkProcessing() {
		return true;
	}

	/**
	 * Override to implement code that gets executed when systems are
	 * initialized.
	 */
	protected void initialize() {}

	/**
	 * Called if the system has received a entity it is interested in, e.g
	 * created or a component was added to it.
	 *
	 * @param e
	 *			the entity that was added to this system
	 */
	protected void inserted(Entity e) {}

	/**
	 * Called if a entity was removed from this system, e.g deleted or had one
	 * of it's components removed.
	 *
	 * @param e
	 *			the entity that was removed from this system
	 */
	protected void removed(Entity e) {}

	/**
	 * Returns true if the system is enabled.
	 * 
	 * @return {@code true} if enabled, otherwise false
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enabled systems are run during {@link #process()}.
	 * <p>
	 * Systems are enabled by default.
	 * </p>
	 * 
	 * @param enabled
	 *			system will not run when set to false
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * A new unique component composition detected, check if this
	 * system's aspect is interested in it.
	 */
	void processComponentIdentity(int id, BitSet componentBits) {
		if (dummy)
			return;

		aspectCache.set(id, aspect.isInterested(componentBits));
	}
	
	/**
	 * Will check if the entity is of interest to this system.
	 *
	 * @param e
	 *			entity to check
	 */
	protected final void check(Entity e) {
		if(dummy)
			return;
		
		EntityManager em = world.getEntityManager();
		int id = e.getId();
		boolean interested = aspectCache.get(em.getIdentity(e)) && em.isActive(id) && em.isEnabled(id);
		boolean contains = activeIds.get(id);
		
		if (interested && !contains) {
			insertToSystem(e);
		} else if (!interested && contains) {
			removeFromSystem(e);
		}
	}
	
	/**
	 * Removes the entity from this system.
	 *
	 * @param e
	 *			the entity to remove
	 */
	private void removeFromSystem(Entity e) {
		actives.remove(e.getId());
		activeIds.clear(e.getId());
		activesIsDirty = true;
		
		removed(e);
	}

	/**
	 * Inserts the entity into this system.
	 *
	 * @param e
	 *			the entity to insert
	 */
	private void insertToSystem(Entity e) {
		activeIds.set(e.getId());
		activesIsDirty = true;
		actives.add(e.getId());
		
		inserted(e);
	}
	
	/**
	 * Call when an entity interesting to the system is added to the world.
	 *
	 * <p>
	 * Checks if the system is interested in the added entity, if so, will
	 * insert it in to the system.
	 * </p>
	 *
	 * @param e
	 *			the added entity
	 */
	@Override
	public final void added(Entity e) {
		check(e);
	}
	
	@Override
	public final void added(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			check((Entity)data[i]);
		}
	}
	
	@Override
	public final void changed(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			check((Entity)data[i]);
		}
	}
	
	@Override
	public final void deleted(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			Entity e = (Entity)data[i];
			if (activeIds.get(e.getId()))
				removeFromSystem(e);
		}
	}

	/**
	 * Call when an entity interesting to the system has changed in the world.
	 * <p>
	 * Checks if the system is still interested after the entity has changed,
	 * e.g a component was removed.
	 * </p>
	 *
	 * @param e
	 *			the changed entity
	 */
	@Override
	public final void changed(Entity e) {
		check(e);
	}

	/**
	 * Call when an entity interesting to the system was deleted from the
	 * world.
	 * <p>
	 * If the deleted entity is in this system, it will be removed.
	 * </p>
	 *
	 * @param e
	 *			the deleted entity
	 */
	@Override
	public final void deleted(Entity e) {
		if(activeIds.get(e.getId()))
			removeFromSystem(e);
	}

	/**
	 * Call when an entity interesting to the system was disabled.
	 *
	 * <p>
	 * If the disabled entity is in this system it will be removed
	 * </p>
	 *
	 * @param e
	 *			the disabled entity
	 */
	@Override
	public final void disabled(Entity e) {
		if(activeIds.get(e.getId()))
			removeFromSystem(e);
	}

	/**
	 * Call when an entity interesting to the system was (re)enabled.
	 * <p>
	 * If the enabled entity is of interest, in will be (re)inserted.
	 * </p>
	 *
	 * @param e
	 *			the (re)enabled entity
	 */
	@Override
	public final void enabled(Entity e) {
		check(e);
	}
	
	/**
	 * Set the world this manager works on.
	 *
	 * @param world
	 *			the world to set
	 */
	protected final void setWorld(World world) {
		if (aspect != null) {
			aspect.initialize(world);
		} else {
			dummy = true;
		}
		
		this.world = world;
	}

	/**
	 * Check if this system is passive.
	 * <p>
	 * A passive system will not process when {@link World#process()}
	 * is called.
	 * </p>
	 *
	 * @return {@code true} if the system is passive
	 */
	public boolean isPassive() {
		return passive;
	}

	/**
	 * Set if the system is passive or not.
	 * <p>
	 * A passive system will not process when {@link World#process()}
	 * is called.
	 * </p>
	 *
	 * @param passive
	 *			{@code true} if passive, {@code false} if not
	 */
	protected void setPassive(boolean passive) {
		this.passive = passive;
	}

	/**
	 * Get all entities being processed by this system.
	 *
	 * @return a bag containing all active entities of the system
	 */
	@Deprecated
	public ImmutableBag<Entity> getActives() {
		if (activesIsDirty && world.isRebuildingIndexAllowed())
			rebuildCompressedActives();

		Bag<Entity> entities = new Bag<Entity>();

		int[] array = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++) {
			entities.add(world.getEntity(array[i]));
		}

		return entities;
	}

	/**
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}
}
