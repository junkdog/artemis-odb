package com.artemis;

import java.util.BitSet;
import java.util.Collection;

import com.artemis.utils.Bag;


/**
 * An Aspect is used by systems as a matcher against entities, to check if a
 * system is interested in an entity.
 * <p>
 * Aspects define what sort of component types an entity must possess, or not
 * possess.
 * </p><p>
 * This creates an aspect where an entity must possess A and B and C:<br />
 * {@code Aspect.getAspectForAll(A.class, B.class, C.class)}
 * </p><p>
 * This creates an aspect where an entity must possess A and B and C, but must
 * not possess U or V.<br />
 * {@code Aspect.getAspectForAll(A.class, B.class, C.class).exclude(U.class, V.class)}
 * </p><p>
 * This creates an aspect where an entity must possess A and B and C, but must
 * not possess U or V, but must possess one of X or Y or Z.<br />
 * {@code Aspect.getAspectForAll(A.class, B.class, C.class).exclude(U.class, V.class).one(X.class, Y.class, Z.class)}
 * </p><p>
 * You can create and compose aspects in many ways:<br />
 * {@code Aspect.getEmpty().one(X.class, Y.class, Z.class).all(A.class, B.class, C.class).exclude(U.class, V.class)}<br />
 * is the same as:<br />
 * {@code Aspect.getAspectForAll(A.class, B.class, C.class).exclude(U.class, V.class).one(X.class, Y.class, Z.class)}
 * </p>
 *
 * @author Arni Arent
 */
public class Aspect {

	/** Component bits the entity must all possess. */
	private BitSet allSet;
	/** Component bits the entity must not possess. */
	private BitSet exclusionSet;
	/** Component bits of which the entity must possess at least one. */
	private BitSet oneSet;
	
	private boolean isInitialized;
	private final Bag<Class<? extends Component>> allTypes;
	private final Bag<Class<? extends Component>> exclusionTypes;
	private final Bag<Class<? extends Component>> oneTypes;


	/**
	 * Aspects can only be created via the static methods
	 * {@link #getAspectForAll}, {@link #getAspectForOne},
	 * or {@link #getEmpty}.
	 */
	private Aspect() {
		allTypes = new Bag<Class<? extends Component>>();
		exclusionTypes = new Bag<Class<? extends Component>>();
		oneTypes = new Bag<Class<? extends Component>>();
	}
	
	public void initialize(World world) {
		if (isInitialized)
			return;
		
		this.allSet = new BitSet();
		this.exclusionSet = new BitSet();
		this.oneSet = new BitSet();
		
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		associate(tf, allTypes, allSet);
		associate(tf, exclusionTypes, exclusionSet);
		associate(tf, oneTypes, oneSet);
		
		allTypes.clear();
		exclusionTypes.clear();
		oneTypes.clear();
		
		isInitialized = true;
	}
	
	private static void associate(ComponentTypeFactory tf, Bag<Class<? extends Component>> types, BitSet componentBits) {
		for (Class<? extends Component> t : types) {
			componentBits.set(tf.getIndexFor(t));
		}
	}


	/**
	 * Get a BitSet containing bits of components the entity must all possess.
	 *
	 * @return
	 *		the "all" BitSet
	 */
	public BitSet getAllSet() {
		return allSet;
	}

	/**
	 * Get a BitSet containing bits of components the entity must not possess.
	 *
	 * @return
	 *		the "exclusion" BitSet
	 */
	public BitSet getExclusionSet() {
		return exclusionSet;
	}

	/**
	 * Get a BitSet containing bits of components of which the entity must
	 * possess atleast one.
	 *
	 * @return
	 *		the "one" BitSet
	 */
	public BitSet getOneSet() {
		return oneSet;
	}

	/**
	 * Returns whether this Aspect would accept the given Entity.
	 */
	public boolean isInterested(Entity e){
		return isInterested(e.getComponentBits());
	}
	
	/**
	 * Returns whether this Aspect would accept the given set.
	 */
	public boolean isInterested(BitSet componentBits){
		
		// Possibly interested, let's try to prove it wrong.
		boolean interested = true;

		// Check if the entity possesses ALL of the components defined in the aspect.
		if(!allSet.isEmpty()) {
			for (int i = allSet.nextSetBit(0); i >= 0; i = allSet.nextSetBit(i+1)) {
				if(!componentBits.get(i)) {
					interested = false;
					break;
				}
			}
		}
		
		// If we are STILL interested,
		// Check if the entity possesses ANY of the exclusion components, if it does then the system is not interested.
		if(interested && !exclusionSet.isEmpty() && interested) {
			interested = !exclusionSet.intersects(componentBits);
		}

		// If we are STILL interested,
		// Check if the entity possesses ANY of the components in the oneSet. If so, the system is interested.
		if(interested && !oneSet.isEmpty()) {
			interested = oneSet.intersects(componentBits);
		}
		
		return interested;
	}
	
	
	/**
	 * Returns an aspect where an entity must possess all of the specified
	 * component types.
	 *
	 * @param types
	 *			a required component type
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect all(Class<? extends Component>... types) {
		for (Class<? extends Component> t : types) {
			allTypes.add(t);
		}

		return this;
	}
	
	
	/**
	 * Returns an aspect where an entity must possess all of the specified
	 * component types.
	 *
	 * @param types
	 *			a required component type
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect all(Collection<Class<? extends Component>> types) {
		for (Class<? extends Component> t : types) {
			allTypes.add(t);
		}

		return this;
	}
	
	/**
	 * Excludes all of the specified component types from the aspect.
	 * <p>
	 * A system will not be interested in an entity that possesses one of the
	 * specified exclusion component types.
	 * </p>
	 *
	 * @param types
	 *			component type to exclude
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect exclude(Class<? extends Component>... types) {
		for (Class<? extends Component> t : types) {
			exclusionTypes.add(t);
		}
		return this;
	}
	
	
	/**
	 * Excludes all of the specified component types from the aspect.
	 * <p>
	 * A system will not be interested in an entity that possesses one of the
	 * specified exclusion component types.
	 * </p>
	 * 
	 * @param types
	 *			component type to exclude
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect exclude(Collection<Class<? extends Component>> types) {
		for (Class<? extends Component> t : types) {
			exclusionTypes.add(t);
		}
		return this;
	}
	
	/**
	 * Returns an aspect where an entity must possess one of the specified
	 * component types.
	 *
	 * @param types
	 *			one of the types the entity must possess
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect one(Class<? extends Component>... types) {
		for (Class<? extends Component> t : types) {
			oneTypes.add(t);
		}
		return this;
	}
	
	/**
	 * Returns an aspect where an entity must possess one of the specified
	 * component types.
	 *
	 * @param types
	 *			one of the types the entity must possess
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect one(Collection<Class<? extends Component>> types) {
		for (Class<? extends Component> t : types) {
			oneTypes.add(t);
		}
		return this;
	}
	
	/**
	 * Creates an aspect where an entity must possess all of the specified
	 * component types.
	 * 
	 * @param types
	 *			a required component type
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public static Aspect getAspectForAll(Class<? extends Component>... types) {
		Aspect aspect = new Aspect();
		aspect.all(types);
		return aspect;
	}
	
	/**
	 * Creates an aspect where an entity must possess one of the specified
	 * component types.
	 * 
	 * @param types
	 *			one of the types the entity must possess
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public static Aspect getAspectForOne(Class<? extends Component>... types) {
		Aspect aspect = new Aspect();
		aspect.one(types);
		return aspect;
	}
	
	/**
	 * Creates and returns an empty aspect.
	 * <p>
	 * This can be used if you want a system that processes no entities, but
	 * still gets invoked. Typical usages is when you need to create special
	 * purpose systems for debug rendering, like rendering FPS, how many
	 * entities are active in the world, etc.
	 * </p><p>
	 * You can also use the all, one and exclude methods on this aspect, so if
	 * you wanted to create a system that processes only entities possessing
	 * just one of the components A or B or C, then you can do:<br />
	 * {@code Aspect.getEmpty().one(A,B,C);}
	 * </p>
	 *
	 * @return an empty Aspect that will reject all entities
	 */
	public static Aspect getEmpty() {
		return new Aspect();
	}
}
