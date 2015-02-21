package com.artemis;

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
public abstract class EntitySystem implements EntitySubscription.SubscriptionListener {

	/** The world this system belongs to. */
	protected World world;
	protected Entity flyweight;
	
	/* 
	 * actives = only contains entities, typically sorted ASC by entity.id 
	 * activesIsDirty = indicates that actives isn't sorted; needs rebuilding 
	 */
	private IntBag actives;

	/** If the system is passive or not. */
	private boolean passive;
	/** If the system is enabled or not. */
	private boolean enabled;
	/** If the system is interested in no entities at all. */
	private Aspect.Builder aspectConfiguration;

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 *
	 * @param aspect
	 *			to match against entities
	 */
	public EntitySystem(Aspect.Builder aspect) {
		this.aspectConfiguration = aspect;
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
			processEntities(actives);
			end();
		}
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

	public void inserted(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			inserted((Entity) data[i]);
		}
	}

	/**
	 * Called if the system has received a entity it is interested in, e.g
	 * created or a component was added to it.
	 *
	 * @param e
	 *			the entity that was added to this system
	 */
	protected void inserted(Entity e) {}

	public void removed(ImmutableBag<Entity> entities) {
		Object[] data = ((Bag<Entity>)entities).getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			removed((Entity) data[i]);
		}
	}

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
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	protected final void check(Entity e) {}

	/**
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	public final void added(Entity e) {}

	/**
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	public final void added(ImmutableBag<Entity> entities) {}

	/**
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	public final void changed(ImmutableBag<Entity> entities) {}

	/**
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	public final void deleted(ImmutableBag<Entity> entities) {}

	/**
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	public final void changed(Entity e) {}

	/**
	 * This method no longer performs any operations due to entity subscriptions lists
	 * being refactored into {@link com.artemis.AspectSubscriptionManager} and
	 * {@link com.artemis.EntitySubscription}.
	 */
	@Deprecated
	public final void deleted(Entity e) {}

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
	@Deprecated
	public final void disabled(Entity e) {}

	/**
	 * Call when an entity interesting to the system was (re)enabled.
	 * <p>
	 * If the enabled entity is of interest, in will be (re)inserted.
	 * </p>
	 *
	 * @param e
	 *			the (re)enabled entity
	 */
	@Deprecated
	public final void enabled(Entity e) {}
	
	/**
	 * Set the world this manager works on.
	 *
	 * @param world
	 *			the world to set
	 */
	protected final void setWorld(World world) {
		if (aspectConfiguration != null) {
			AspectSubscriptionManager sm = world.getManager(AspectSubscriptionManager.class);
			EntitySubscription subscription = sm.get(aspectConfiguration);
			subscription.addSubscriptionListener(this);
			actives = subscription.getEntities();
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
	 * @deprecated This method allocates a new Bag each time, refer to {@link #getActives(com.artemis.utils.Bag)}
	 */
	@Deprecated
	public ImmutableBag<Entity> getActives() {
		return getActives(new Bag<Entity>());
	}

	/**
	 * Get all entities being processed by this system.
	 *
	 * @return a bag containing all active entities of the system
	 */
	public Bag<Entity> getActives(Bag<Entity> fillBag) {
		int[] array = actives.getData();
		for (int i = 0, s = actives.size(); s > i; i++) {
			fillBag.add(world.getEntity(array[i]));
		}

		return fillBag;
	}

	/**
	 * see {@link World#dispose()}
	 */
	protected void dispose() {}
}
