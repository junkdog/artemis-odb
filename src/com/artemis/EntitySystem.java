package com.artemis;

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
public abstract class EntitySystem {
	private long systemBit;

	private long typeFlags;

	protected World world;

	private Bag<Entity> actives;
	
	private boolean passive;
	
	protected EntitySystem(Aspect aspect) {
		actives = new Bag<Entity>();
		typeFlags = aspect.getTypeFlags();
		systemBit = SystemBitManager.getBitFor(this.getClass());
	}
	
	protected void setSystemBit(long bit) {
		this.systemBit = bit;
	}
	
	/**
	 * Called before processing of entities begins. 
	 */
	protected void begin() {
	};

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
	};
	
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
	protected void added(Entity e) {};

	/**
	 * Called if a entity was removed from this system, e.g. deleted or had one of it's components removed.
	 * @param e the entity that was removed from this system.
	 */
	protected void removed(Entity e) {};

	protected final void change(Entity e) {
		boolean contains = (systemBit & e.getSystemBits()) == systemBit;
		boolean interest = (typeFlags & e.getTypeBits()) == typeFlags;

		if (interest && !contains && typeFlags > 0) {
			actives.add(e);
			e.addSystemBit(systemBit);
			added(e);
		} else if (!interest && contains && typeFlags > 0) {
			remove(e);
		}
	}

	private void remove(Entity e) {
		actives.remove(e);
		e.removeSystemBit(systemBit);
		removed(e);
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
	 * 
	 * @author Arni Arent
	 *
	 */
	private static class SystemBitManager {
		private static int POS = 0;
		private static HashMap<Class<? extends EntitySystem>, Long> systemBits = new HashMap<Class<? extends EntitySystem>, Long>();
		
		protected static final long getBitFor(Class<? extends EntitySystem> es){
			Long bit = systemBits.get(es);
			
			if(bit == null){
				bit = 1L << POS;
				POS++;
				systemBits.put(es, bit);
			}
			
			return bit;
		}
	}

}
