package com.artemis;

import java.util.BitSet;
import java.util.HashMap;

import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

/**
 * The most raw entity system. It should not typically be used, but you can create your own
 * entity system handling by extending this. It is recommended that you use the other provided
 * entity system implementations.
 * 
 * @author Arni Arent
 *
 */
public abstract class EntitySystem implements EntityObserver {
	private final int systemIndex;

	private BitSet systemAspect;
	
	private BitSet exclusionAspect;

	protected World world;

	private Bag<Entity> actives;
	
	private boolean passive;
	
	public EntitySystem(Aspect aspect) {
		actives = new Bag<Entity>();
		systemAspect = aspect.getBitSet();
		exclusionAspect = aspect.getExclusionSet();
		systemIndex = SystemIndexManager.getIndexFor(this.getClass());
	}
	
	/**
	 * Called before processing of entities begins. 
	 */
	protected void begin() {
	}

	public final void process() {
		if(checkProcessing()) {
			begin();
			processEntities(actives);
			end();
		}
	}
	
	/**
	 * Called after the processing of entities ends.
	 */
	protected void end() {
	}
	
	/**
	 * Any implementing entity system must implement this method and the logic
	 * to process the given entities of the system.
	 * 
	 * @param entities the entities this system contains.
	 */
	protected abstract void processEntities(ImmutableBag<Entity> entities);
	
	/**
	 * 
	 * @return true if the system should be processed, false if not.
	 */
	protected abstract boolean checkProcessing();

	/**
	 * Override to implement code that gets executed when systems are initialized.
	 */
	protected void initialize() {};

	/**
	 * Called if the system has received a entity it is interested in, e.g. created or a component was added to it.
	 * @param e the entity that was added to this system.
	 */
	protected void inserted(Entity e) {};

	/**
	 * Called if a entity was removed from this system, e.g. deleted or had one of it's components removed.
	 * @param e the entity that was removed from this system.
	 */
	protected void removed(Entity e) {};

	/**
	 * Will check if the entity is of interest to this system.
	 * @param e entity to check
	 */
	protected final void check(Entity e) {
		boolean systemContainsEntity = e.getSystemBits().get(systemIndex);
		boolean systemIsInterestedInEntity = true;
		
		BitSet componentBits = e.getComponentBits();
		for (int i = systemAspect.nextSetBit(0); i >= 0; i = systemAspect.nextSetBit(i+1)) {
			if(!componentBits.get(i)) {
				systemIsInterestedInEntity = false;
				break;
			}
		}
		
		if(!exclusionAspect.isEmpty()) {
			for (int i = exclusionAspect.nextSetBit(0); i >= 0; i = exclusionAspect.nextSetBit(i+1)) {
				if(componentBits.get(i)) {
					systemIsInterestedInEntity = false;
					break;
				}
			}
		}

		if (systemIsInterestedInEntity && !systemContainsEntity && !systemAspect.isEmpty()) {
			insertToSystem(e);
		} else if (!systemIsInterestedInEntity && systemContainsEntity && !systemAspect.isEmpty()) {
			removeFromSystem(e);
		}
	}

	private void removeFromSystem(Entity e) {
		actives.remove(e);
		e.getSystemBits().clear(systemIndex);
		removed(e);
	}

	private void insertToSystem(Entity e) {
		actives.add(e);
		e.getSystemBits().set(systemIndex);
		inserted(e);
	}
	
	
	@Override
	public final void added(Entity e) {
		check(e);
	}
	
	@Override
	public final void changed(Entity e) {
		check(e);
	}
	
	@Override
	public final void deleted(Entity e) {
		if(e.getSystemBits().get(systemIndex)) {
			removeFromSystem(e);
		}
	}
	
	@Override
	public final void disabled(Entity e) {
		if(e.getSystemBits().get(systemIndex)) {
			removeFromSystem(e);
		}
	}
	
	@Override
	public final void enabled(Entity e) {
		check(e);
	}
	

	protected final void setWorld(World world) {
		this.world = world;
	}
	
	protected boolean isPassive() {
		return passive;
	}

	protected void setPassive(boolean passive) {
		this.passive = passive;
	}
	
	
	
	/**
	 * Used to generate a unique bit for each system.
	 * Only used internally in EntitySystem.
	 */
	private static class SystemIndexManager {
		private static int INDEX = 0;
		private static HashMap<Class<? extends EntitySystem>, Integer> indices = new HashMap<Class<? extends EntitySystem>, Integer>();
		
		private static int getIndexFor(Class<? extends EntitySystem> es){
			Integer index = indices.get(es);
			if(index == null) {
				index = INDEX++;
				indices.put(es, index);
			}
			return index;
		}
	}

}
