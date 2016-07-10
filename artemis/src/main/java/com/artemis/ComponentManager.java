package com.artemis;

import java.util.BitSet;

import com.artemis.annotations.SkipWire;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.IntBag;
import com.artemis.utils.ShortBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionException;


/**
 * Handles the association between entities and their components.
 * <p>
 * Only one component manager exists per {@link World} instance,
 * managed by the world.
 * </p>
 *
 * @author Arni Arent
 */
@SkipWire
public class ComponentManager extends BaseSystem {
	/** Adrian's secret rebellion. */
	static final int NO_COMPONENTS = 0;

	/** Collects all Entites marked for deletion from this ComponentManager. */
	private Bag<ComponentMapper> mappers = new Bag(ComponentMapper.class);

	private final ComponentIdentityResolver identityResolver = new ComponentIdentityResolver();
	final ShortBag entityToIdentity;
	protected final ComponentTypeFactory typeFactory;

	/**
	 * Creates a new instance of {@link ComponentManager}.
	 */
	protected ComponentManager(int entityContainerSize) {
		entityToIdentity = new ShortBag(entityContainerSize);
		typeFactory = new ComponentTypeFactory(this, entityContainerSize);
	}

	@Override
	protected void processSystem() {}

	/**
	 * Create a component of given type by class.
	 * @param owner entity id
	 * @param componentClass class of component to instance.
	 * @return Newly created packed, pooled or basic component.
	 */
	protected <T extends Component> T create(int owner, Class<T> componentClass) {
		return getMapper(componentClass).create(owner);
	}

	protected <T extends Component> ComponentMapper<T> getMapper(Class<T> mapper) {
		ComponentType type = typeFactory.getTypeFor(mapper);
		return mappers.get(type.getIndex());
	}

	void registerComponentType(ComponentType ct, int capacity) {
		int index = ct.getIndex();
		ComponentMapper mapper = new ComponentMapper(ct.getType(), world);
		mapper.components.ensureCapacity(capacity);
		mappers.set(index, mapper);
	}

	@SuppressWarnings("unchecked")
	static <T extends Component> T newInstance(Class<T> componentClass) {
		try {
			return ClassReflection.newInstance(componentClass);
		} catch (ReflectionException e) {
			throw new InvalidComponentException(componentClass, "Unable to instantiate component.", e);
		}
	}


	/**
	 * Removes all components from deleted entities.
	 *
	 * @param pendingPurge
	 *			the entities to remove components from
	 */
	void clean(IntBag pendingPurge) {
		int[] ids = pendingPurge.getData();
		for (int i = 0, s = pendingPurge.size(); s > i; i++) {
			removeComponents(ids[i]);
		}
	}

	private void removeComponents(int entityId) {
		Bag<ComponentMapper> mappers = componentMappers(entityId);
		for (int i = 0, s = mappers.size(); s > i; i++) {
			mappers.get(i).internalRemove(entityId);
		}

		setIdentity(entityId, 0);
	}

	/**
	 * Get all components from all entities for a given type.
	 *
	 * @param type
	 *			the type of components to get
	 * @return a bag containing all components of the given type
	 */
	protected Bag<Component> getComponentsByType(ComponentType type) {
		return mappers.get(type.getIndex()).components;
	}

   /**
	 * @return Bag of all generated component types, which identify components without having to use classes.
	 */
	public ImmutableBag<ComponentType> getComponentTypes() {
		return typeFactory.types;
	}

	/**
	 * Get a component of an entity.
	 *
	 * @param entityId
	 *			the entity associated with the component
	 * @param type
	 *			the type of component to get
	 * @return the component of given type
	 */
	protected Component getComponent(int entityId, ComponentType type) {
		ComponentMapper mapper = mappers.get(type.getIndex());
		return mapper.get(entityId);
	}

	/**
	 * Get all component associated with an entity.
	 *
	 * @param entityId
	 *			the entity to get components from
	 * @param fillBag
	 *			a bag to be filled with components
	 * @return the {@code fillBag}, filled with the entities components
	 */
	public Bag<Component> getComponentsFor(int entityId, Bag<Component> fillBag) {
		Bag<ComponentMapper> mappers = componentMappers(entityId);

		for (int i = 0, s = mappers.size(); s > i; i++) {
			fillBag.add(mappers.get(i).get(entityId));
		}

		return fillBag;
	}

	/** Get component composition of entity. */
	BitSet componentBits(int entityId) {
		int identityIndex = entityToIdentity.get(entityId);
		return identityResolver.compositionBits.get(identityIndex);
	}

	/** Get component composition of entity. */
	private Bag<ComponentMapper> componentMappers(int entityId) {
		int identityIndex = entityToIdentity.get(entityId);
		return identityResolver.compositionMappers.get(identityIndex);
	}

	/**
	 * Fetches unique identifier for composition.
	 *
	 * @param componentBits composition to fetch unique identifier for.
	 * @return Unique identifier for passed composition.
	 */
	public int compositionIdentity(BitSet componentBits) {
		int identity = identityResolver.getIdentity(componentBits);
		if (identity == -1) {
			identity = identityResolver.allocateIdentity(componentBits, this);
			world.getAspectSubscriptionManager()
				.processComponentIdentity(identity, componentBits);
		}

		return identity;
	}

	/**
	 * Fetch composition id for entity.
	 *
	 * A composition id is uniquely identified by a single Aspect. For performance reasons, each entity is
	 * identified by its composition id. Adding or removing components from an entity will change its compositionId.
	 *
	 * @param entityId
	 * @return composition identity.
	 */
	protected int getIdentity(int entityId) {
		return entityToIdentity.get(entityId);
	}

	/**
	 * Synchronizes new subscriptions with {@link World} state.
	 *
	 * @param es entity subscription to update.
	 */
	void synchronize(EntitySubscription es) {
		Bag<BitSet> compositionBits = identityResolver.compositionBits;
		for (int i = 1, s = compositionBits.size(); s > i; i++) {
			BitSet componentBits = compositionBits.get(i);
			es.processComponentIdentity(i, componentBits);
		}

		for (Entity e : world.getEntityManager().entities) {
			if (e != null) es.check(e.id, getIdentity(e.id));
		}

		es.informEntityChanges();
		es.rebuildCompressedActives();
	}

	/**
	 * Set composition id of entity.
	 *
	 * @param entityId entity id
	 * @param compositionId composition id
	 */
	void setIdentity(int entityId, int compositionId) {
		entityToIdentity.unsafeSet(entityId, (short) compositionId);
	}

	/**
	 * @return Factory responsible for tracking all component types.
	 */
	public ComponentTypeFactory getTypeFactory() {
		return typeFactory;
	}

	public void ensureCapacity(int newSize) {
		typeFactory.initialMapperCapacity = newSize;
		entityToIdentity.ensureCapacity(newSize);
		for (ComponentMapper mapper : mappers) {
			mapper.components.ensureCapacity(newSize);
		}
	}

	/** Tracks all unique component compositions. */
	static final class ComponentIdentityResolver {
		final Bag<BitSet> compositionBits;
		final Bag<Bag<ComponentMapper>> compositionMappers;

		ComponentIdentityResolver() {
			compositionBits = new Bag(BitSet.class);
			compositionBits.add(new BitSet());
			compositionMappers = new Bag<Bag<ComponentMapper>>();
			compositionMappers.add(new Bag(ComponentMapper.class));
		}

		/** Fetch unique identity for passed composition. */
		int getIdentity(BitSet components) {
			BitSet[] bitsets = compositionBits.getData();
			int size = compositionBits.size();
			for (int i = NO_COMPONENTS; size > i; i++) { // want to start from 1 so that 0 can mean null
				if (components.equals(bitsets[i]))
					return i;
			}

			return -1;
		}

		int allocateIdentity(BitSet componentBits, ComponentManager cm) {
			Bag<ComponentMapper> mappers =
				new Bag<ComponentMapper>(ComponentMapper.class, componentBits.cardinality());

			ComponentTypeFactory tf = cm.getTypeFactory();
			for (int i = componentBits.nextSetBit(0); i >= 0; i = componentBits.nextSetBit(i + 1)) {
				mappers.add(cm.getMapper(tf.getTypeFor(i).getType()));
			}

			compositionMappers.add(mappers);
			compositionBits.add((BitSet)componentBits.clone());

			return compositionBits.size() - 1;
		}
	}
}
