package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;

/**
 * Builder for basic Archetype instances. To reap the maximum benefit of Archetypes,
 * it's recommended to stash them away inside an manager or similar. Archetypes
 * main advantage come from the improved insertion into systems and managers performance.
 * Calling {@link Entity#edit() edit()} on the Entity returned by {@link World#createEntity(Archetype)}
 * nullifies this optimization.  
 * <p>
 * Generated archetypes provide a blueprint for quick entity creation.
 * Instance generated entities using {@link World#createEntity(Archetype)}
 * 
 * @since 0.7
 */
public class ArchetypeBuilder {
	private final Bag<Class<? extends Component>> classes;

	/**
	 * Constructs an archetype builder containing the composition of the specified parent.
	 *
	 * @param parent archetype composition to copy.
	 */
	public ArchetypeBuilder(Archetype parent) {
		classes = new Bag<Class<? extends Component>>();
		if (parent == null)
			return;
		
		for (int i = 0; parent.types.length > i; i++)
			classes.add(parent.types[i].getType());
	}

	/**
	 * Constructs an empty archetype builder.
	 */
	public ArchetypeBuilder() {
		this(null);
	}

	/**
	 * Ensure this builder includes the specified component type.
	 *
	 * @param type
	 * @return This instance for chaining.
	 */
	public ArchetypeBuilder add(Class<? extends Component> type) {
		if (!classes.contains(type))
			classes.add(type);

		return this;
	}
	
	/**
	 * Ensure this builder includes the specified component types.
	 *
	 * @param types
	 * @return This instance for chaining.
	 */
	public ArchetypeBuilder add(Class<? extends Component>... types) {
		for (int i = 0; types.length > i; i++) {
			Class<? extends Component> type = types[i];
			
			if (!classes.contains(type))
				classes.add(type);
		}
		
		return this;
	}

	/**
	 * Remove the specified component from this builder, if it is present (optional operation).
	 *
 	 * @param type
	 * @return This instance for chaining.
	 */
	public ArchetypeBuilder remove(Class<? extends Component> type) {
		classes.remove(type);
		return this;
	}
	
	/**
	 * Remove the specified component from this builder, if it is present (optional operation).
	 *
	 * @param types
	 * @return This instance for chaining.
	 */
	public ArchetypeBuilder remove(Class<? extends Component>... types) {
		for (int i = 0; types.length > i; i++) {
			classes.remove(types[i]);
		}
		
		return this;
	}

	/**
	 * Create a new world specific instance of Archetype based on the current state.
	 *
	 * @param world applicable domain of the Archetype.
	 * @return new Archetype based on current state
	 */
	public Archetype build(World world) {
		ComponentType[] types = resolveTypes(world);
		EntityManager em = world.getEntityManager();
		return new Archetype(types, em.compositionIdentity(bitset(types)));
	}
	
	/** generate bitset mask of types. */
	private static BitSet bitset(ComponentType[] types) {
		BitSet bs = new BitSet();
		for (int i = 0; types.length > i; i++)
			bs.set(types[i].getIndex());
			
		return bs;
	}

	/** Converts java classes to component types. */
	private ComponentType[] resolveTypes(World world) {
		ComponentManager cm = world.getComponentManager();
		if (cm == null)
			throw new MundaneWireException("World#initialize() has not been called.");
		
		ComponentTypeFactory tf = cm.typeFactory;
		ComponentType[] types = new ComponentType[classes.size()];
		for (int i = 0, s = classes.size(); s > i; i++)
			types[i] = tf.getTypeFor(classes.get(i));
		
		return types;
	}
}