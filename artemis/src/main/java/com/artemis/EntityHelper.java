package com.artemis;

import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.Bag;

import java.util.BitSet;
import java.util.UUID;


/**
 * Helper class for Entities.
 */
@Deprecated
public final class EntityHelper {
	public static final int NO_ENTITY = -1;

	//#include "./entity_flyweight_bool.inc"

	/**
	 * Returns a BitSet instance containing bits of the components the entity
	 * possesses.
	 * @return a BitSet containing the entities component bits
	 */
	protected static BitSet getComponentBits(World world, int id) {
		return world.getEntityManager().componentBits(id);
	}

	/**
	 * Checks if the entity has been added to the world and has not been
	 * deleted from it.
	 * <p>
	 * If the entity has been disabled this will still return true.
	 * </p>
	 * @return {@code true} if it's active
	 */
	public static boolean isActive(World world, int id) {
		return world.getEntityManager().isActive(id);
	}

	/**
	 * Retrieves component from this entity.
	 * <p>
	 * It will provide good performance. But the recommended way to retrieve
	 * components from an entity is using the ComponentMapper.
	 * </p>
	 * @param type
	 * 		in order to retrieve the component fast you must provide a
	 * 		ComponentType instance for the expected component
	 * @return
	 */
	public static Component getComponent(ComponentType type, World world, int id) {
		return world.getComponentManager().getComponent(id, type);
	}

	/**
	 * Slower retrieval of components from this entity.
	 * <p>
	 * Minimize usage of this, but is fine to use e.g. when creating new
	 * entities and setting data in components.
	 * </p>
	 * @param <T>
	 * 		the expected return component class type
	 * @param type
	 * 		the expected return component class type
	 * @return component that matches, or null if none is found
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> T getComponent(Class<T> type, World world, int id) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		return (T) getComponent(tf.getTypeFor(type), world, id);
	}

	/**
	 * Returns a bag of all components this entity has.
	 * <p>
	 * You need to reset the bag yourself if you intend to fill it more than
	 * once.
	 * </p>
	 * @param fillBag
	 * 		the bag to put the components into
	 * @return the fillBag containing the components
	 */
	public static Bag<Component> getComponents(Bag<Component> fillBag, World world, int id) {
		return world.getComponentManager().getComponentsFor(id, fillBag);
	}

	/**
	 * @return unique identifier for entities with this specific component configuration.
	 */
	public static int getCompositionId(World world, int id) {
		return world.getEntityManager().getIdentity(id);
	}

	/**
	 * Edit entity.
	 *
	 * @param id
	 * @return EntityHelper Editor for id.
	 */
	public static EntityEdit edit(World world, int id) {
		return world.editPool.obtainEditor(id);
	}

}
