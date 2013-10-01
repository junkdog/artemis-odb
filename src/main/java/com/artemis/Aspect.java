package com.artemis;

import java.util.BitSet;


/**
 * An Aspects is used by systems as a matcher against entities, to check if a
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

	/**
	 * Contains bits of components the entity must all possess.
	 */
	private final BitSet allSet;
	/**
	 * Contains bits of components the entity must not possess.
	 */
	private final BitSet exclusionSet;
	/**
	 * Contains bits of components of which the entity must possess at least
	 * one.
	 */
	private final BitSet oneSet;

	/**
	 * Aspects can only be created via the static methods
	 * {@link #getAspectForAll}, {@link #getAspectForOne},
	 * or {@link #getEmpty}.
	 */
	private Aspect() {
		this.allSet = new BitSet();
		this.exclusionSet = new BitSet();
		this.oneSet = new BitSet();
	}

	/**
	 * Get a BitSet containing bits of components the entity must all possess.
	 *
	 * @return
	 *		the "all" BitSet
	 */
	protected BitSet getAllSet() {
		return allSet;
	}

	/**
	 * Get a BitSet containing bits of components the entity must not possess.
	 *
	 * @return
	 *		the "exclusion" BitSet
	 */
	protected BitSet getExclusionSet() {
		return exclusionSet;
	}

	/**
	 * Get a BitSet containing bits of components of which the entity must
	 * possess atleast one.
	 *
	 * @return
	 *		the "one" BitSet
	 */
	protected BitSet getOneSet() {
		return oneSet;
	}
	
	/**
	 * Returns an aspect where an entity must possess all of the specified
	 * component types.
	 *
	 * @param type
	 *			a required component type
	 * @param types
	 *			a required component type
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect all(Class<? extends Component> type, Class<? extends Component>... types) {
		allSet.set(ComponentType.getIndexFor(type));
		
		for (Class<? extends Component> t : types) {
			allSet.set(ComponentType.getIndexFor(t));
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
	 * @param type
	 *			component type to exclude
	 * @param types
	 *			component type to exclude
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect exclude(Class<? extends Component> type, Class<? extends Component>... types) {
		exclusionSet.set(ComponentType.getIndexFor(type));
		
		for (Class<? extends Component> t : types) {
			exclusionSet.set(ComponentType.getIndexFor(t));
		}
		return this;
	}
	
	/**
	 * Returns an aspect where an entity must possess one of the specified
	 * component types.
	 *
	 * @param type
	 *			one of the types the entity must possess
	 * @param types
	 *			one of the types the entity must possess
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public Aspect one(Class<? extends Component> type, Class<? extends Component>... types) {
		oneSet.set(ComponentType.getIndexFor(type));
		
		for (Class<? extends Component> t : types) {
			oneSet.set(ComponentType.getIndexFor(t));
		}
		return this;
	}
	
	/**
	 * Creates an aspect where an entity must possess all of the specified
	 * component types.
	 * 
	 * @param type
	 *			the type the entity must possess
	 * @param types
	 *			the type the entity must possess
	 *
	 * @return an aspect that can be matched against entities
	 * 
	 * @deprecated
	 * 
	 * @see getAspectForAll
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static Aspect getAspectFor(Class<? extends Component> type, Class<? extends Component>... types) {
		return getAspectForAll(type, types);
	}
	
	/**
	 * Creates an aspect where an entity must possess all of the specified
	 * component types.
	 * 
	 * @param type
	 *			a required component type
	 * @param types
	 *			a required component type
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public static Aspect getAspectForAll(Class<? extends Component> type, Class<? extends Component>... types) {
		Aspect aspect = new Aspect();
		aspect.all(type, types);
		return aspect;
	}
	
	/**
	 * Creates an aspect where an entity must possess one of the specified
	 * component types.
	 * 
	 * @param type
	 *			one of the types the entity must possess
	 * @param types
	 *			one of the types the entity must possess
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public static Aspect getAspectForOne(Class<? extends Component> type, Class<? extends Component>... types) {
		Aspect aspect = new Aspect();
		aspect.one(type, types);
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
