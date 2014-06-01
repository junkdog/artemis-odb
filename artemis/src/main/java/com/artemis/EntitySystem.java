package com.artemis;

import java.util.BitSet;
import java.util.IdentityHashMap;

import com.artemis.utils.ImmutableBag;


/**
 * The most raw entity system.
 * <p>
 * It should not typically be used, but you can create your own entity system
 * handling by extending this. It is recommended that you use the other
 * provided entity system implementations.
 * </p>
 *
 * @author Arni Arent
 */
public abstract class EntitySystem implements EntityObserver {

	/** The system's index in the SystemIndexManager. */
	private final int systemIndex;
	/** The world this system belongs to. */
	protected World world;
	
	/* 
	 * actives = only contains entities, sorted ASC by entity.id 
	 * activesIsDirty = indicates that compressedActives needs rebuilding 
	 */
	private final WildBag<Entity> actives;
	private final BitSet activeIds;
	private boolean activesIsDirty;
	
	/** Collects entities to be deleted from the system after processing. */
	private final WildBag<Entity> delayedDeletion;
	/** Component bits entities must possess for the system to be interested. */
	private final BitSet allSet;
	/** Component bits entities must not possess for the system to be interested. */
	private final BitSet exclusionSet;
	/** Component bits entities must at least possess one for the system to be interested. */
	private final BitSet oneSet;
	/** If the system is passive or not. */
	private boolean passive;
	/** If the system is enabled or not. */
	private boolean enabled;
	/** If the system is interested in no entities at all. */
	private final boolean dummy;
	/** If the system is currently processing. */
	private boolean isProcessing;

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public EntitySystem(Aspect aspect) {
		activeIds = new BitSet();
		actives = new WildBag<Entity>();
		
		delayedDeletion = new WildBag<Entity>();
		allSet = aspect.getAllSet();
		exclusionSet = aspect.getExclusionSet();
		oneSet = aspect.getOneSet();
		systemIndex = SystemIndexManager.getIndexFor(this.getClass());
		dummy = allSet.isEmpty() && oneSet.isEmpty(); // This system can't possibly be interested in any entity, so it must be "dummy"
		
		enabled = true;
		isProcessing = false;
	}


	/**
	 * Called before processing of entities begins. 
	 */
	protected void begin() {}

	/**
	 * Process all entities this system is interested in.
	 */
	public final void process() {
		if(enabled && checkProcessing()) {
			begin();
			
			if (activesIsDirty)
				rebuildCompressedActives();
			
			isProcessing = true;
			processEntities(actives);
			isProcessing = false;

			int s = delayedDeletion.size();
			if (s > 0) {
				Object[] data = delayedDeletion.getData();
				for (int i = 0; i < s; i++) {
					removeFromSystem((Entity)data[i]);
					data[i] = null;
				}
				delayedDeletion.setSize(0);
			}
			
			end();
		}
	}
	
	private void rebuildCompressedActives() {
		actives.setSize(0);
		
		BitSet bs = activeIds;
		EntityManager em = world.getEntityManager();
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			actives.add(em.getEntity(i));
		}
		
		activesIsDirty = false;
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
	protected abstract void processEntities(ImmutableBag<Entity> entities);
	
	/**
	 * Check if the system should be processed.
	 *
	 * @return true if the system should be processed, false if not.
	 */
	protected abstract boolean checkProcessing();

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
	 * Will check if the entity is of interest to this system.
	 *
	 * @param e
	 *			entity to check
	 */
	protected final void check(Entity e) {
		if(dummy) {
			return;
		}
		
		boolean contains = e.getSystemBits().get(systemIndex);
		boolean interested = true; // possibly interested, let's try to prove it wrong.
		
		BitSet componentBits = e.getComponentBits();

		// Check if the entity possesses ALL of the components defined in the aspect.
		if(!allSet.isEmpty()) {
			for (int i = allSet.nextSetBit(0); i >= 0; i = allSet.nextSetBit(i+1)) {
				if(!componentBits.get(i)) {
					interested = false;
					break;
				}
			}
		}
		
		// Check if the entity possesses ANY of the exclusion components, if it does then the system is not interested.
		if(!exclusionSet.isEmpty() && interested) {
			interested = !exclusionSet.intersects(componentBits);
		}
		
		// Check if the entity possesses ANY of the components in the oneSet. If so, the system is interested.
		if(!oneSet.isEmpty() && interested) {
			interested = oneSet.intersects(componentBits);
		}

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
		if (isProcessing) {
			delayedDeletion.add(e);
			return;
		}
		
		activeIds.clear(e.getId());
		activesIsDirty = true;
		
		e.getSystemBits().clear(systemIndex);
		removed(e);
	}

	/**
	 * Insterts the entity into this system.
	 *
	 * @param e
	 *			the entity to insert
	 */
	private void insertToSystem(Entity e) {
		activeIds.set(e.getId());
		activesIsDirty = true;
		
		e.getSystemBits().set(systemIndex);
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
		if(e.getSystemBits().get(systemIndex)) {
			removeFromSystem(e);
		}
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
		if(e.getSystemBits().get(systemIndex)) {
			removeFromSystem(e);
		}
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
	public ImmutableBag<Entity> getActives() {
		if (activesIsDirty)
			rebuildCompressedActives();
		
		return actives;
	}

	/**
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}

	/**
	 * Used to generate a unique bit for each system.
	 * <p>
	 * Only used internally in EntitySystem.
	 * </p>
	 */
	private static final class SystemIndexManager {

		/** Amount of EntitySystem indices. */
		private static int INDEX = 0;
		
		/**
		 * Contains the class types of all created systems.
		 * <p>
		 * Only one system per class is permitted in the world.
		 * </p>
		 */
		private static final IdentityHashMap<Class<? extends EntitySystem>, Integer> indices
				= new IdentityHashMap<Class<? extends EntitySystem>, Integer>();

		/**
		 * Called by the EntitySystem constructor.
		 * Will give the new EntitySystem the next index, and store the systems
		 * class as an (Index, Class) entry.
		 *
		 * @param es
		 *			the systems class type
		 *
		 * @return the systems index
		 */
		private static int getIndexFor(Class<? extends EntitySystem> es) {
			Integer index = indices.get(es);
			if(index == null) {
				index = INDEX++;
				indices.put(es, index);
			}
			return index;
		}
	}
}
