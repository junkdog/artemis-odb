package com.artemis;

import com.artemis.utils.BitVector;
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
 * {@code Aspect.all(A.class, B.class, C.class)}
 * </p><p>
 * This creates an aspect where an entity must possess A and B and C, but must
 * not possess U or V.<br />
 * {@code Aspect.all(A.class, B.class, C.class).exclude(U.class, V.class)}
 * </p><p>
 * This creates an aspect where an entity must possess A and B and C, but must
 * not possess U or V, but must possess one of X or Y or Z.<br />
 * {@code Aspect.all(A.class, B.class, C.class).exclude(U.class, V.class).one(X.class, Y.class, Z.class)}
 * </p><p>
 * You can create and compose aspects in many ways:<br />
 * {@code Aspect.one(X.class, Y.class, Z.class).all(A.class, B.class, C.class).exclude(U.class, V.class)}<br />
 * is the same as:<br />
 * {@code Aspect.all(A.class, B.class, C.class).exclude(U.class, V.class).one(X.class, Y.class, Z.class)}
 * </p>
 *
 * @author Arni Arent
 */
public class Aspect {

	/** Component bits the entity must all possess. */
	BitVector allSet;
	/** Component bits the entity must not possess. */
	BitVector exclusionSet;
	/** Component bits of which the entity must possess at least one. */
	BitVector oneSet;

	private Aspect() {
		this.allSet = new BitVector();
		this.exclusionSet = new BitVector();
		this.oneSet = new BitVector();
	}

	/**
	 * Get a BitVector containing bits of components the entity must all possess.
	 *
	 * @return
	 *		the "all" BitVector
	 */
	public BitVector getAllSet() {
		return allSet;
	}

	/**
	 * Get a BitVector containing bits of components the entity must not possess.
	 *
	 * @return
	 *		the "exclusion" BitVector
	 */
	public BitVector getExclusionSet() {
		return exclusionSet;
	}

	/**
	 * Get a BitVector containing bits of components of which the entity must
	 * possess atleast one.
	 *
	 * @return
	 *		the "one" BitVector
	 */
	public BitVector getOneSet() {
		return oneSet;
	}

	/**
	 * Returns whether this Aspect would accept the given set.
	 */
	public boolean isInterested(BitVector componentBits){
		// Check if the entity possesses ALL of the components defined in the aspect.
		if(!allSet.isEmpty()) {
			for (int i = allSet.nextSetBit(0); i >= 0; i = allSet.nextSetBit(i+1)) {
				if(!componentBits.get(i)) {
					return false;
				}
			}
		}

		// If we are STILL interested,
		// Check if the entity possesses ANY of the exclusion components,
		// if it does then the system is not interested.
		if (!exclusionSet.isEmpty() && exclusionSet.intersects(componentBits))
			return false;

		// If we are STILL interested,
		// Check if the entity possesses ANY of the components in the oneSet.
		// If so, the system is interested.
		if (!oneSet.isEmpty() && !oneSet.intersects(componentBits))
			return false;

		return true;
	}

	/**
	 * Returns an aspect that matches all entities.
	 *
	 * @return an aspect that can be matched against entities
	 */
	@SuppressWarnings("unchecked")
	public static Aspect.Builder all() {
		return new Builder().all();
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
	@SafeVarargs
	public static Aspect.Builder all(Class<? extends Component>... types) {
		return new Builder().all(types);
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
	public static Aspect.Builder all(Collection<Class<? extends Component>> types) {
		return new Builder().all(types);
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
	@SafeVarargs
	public static Aspect.Builder exclude(Class<? extends Component>... types) {
		return new Builder().exclude(types);
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
	public static Aspect.Builder exclude(Collection<Class<? extends Component>> types) {
		return new Builder().exclude(types);
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
	@SafeVarargs
	public static Aspect.Builder one(Class<? extends Component>... types) {
		return new Builder().one(types);
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
	public static Aspect.Builder one(Collection<Class<? extends Component>> types) {
		return new Builder().one(types);
	}

	/**
	 * Constructs instances of {@link Aspect}.
	 */
	public static class Builder {
		private final Bag<Class<? extends Component>> allTypes;
		private final Bag<Class<? extends Component>> exclusionTypes;
		private final Bag<Class<? extends Component>> oneTypes;

		private Builder() {
			allTypes = new Bag<Class<? extends Component>>();
			exclusionTypes = new Bag<Class<? extends Component>>();
			oneTypes = new Bag<Class<? extends Component>>();
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
		public Builder all(Class<? extends Component>... types) {
			for (Class<? extends Component> t : types) {
				allTypes.add(t);
			}
			return this;
		}

		/**
		 * @return new instance of this builder.
		 */
		public Builder copy() {
			Builder b = new Builder();
			b.allTypes.addAll(allTypes);
			b.exclusionTypes.addAll(exclusionTypes);
			b.oneTypes.addAll(oneTypes);
			return b;
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
		public Builder all(Collection<Class<? extends Component>> types) {
			for (Class<? extends Component> t : types) {
				allTypes.add(t);
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
		public Builder one(Class<? extends Component>... types) {
			for (Class<? extends Component> t : types)
				oneTypes.add(t);

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
		public Builder one(Collection<Class<? extends Component>> types) {
			for (Class<? extends Component> t : types)
				oneTypes.add(t);

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
		public Builder exclude(Class<? extends Component>... types) {
			for (Class<? extends Component> t : types)
				exclusionTypes.add(t);
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
		public Builder exclude(Collection<Class<? extends Component>> types) {
			for (Class<? extends Component> t : types)
				exclusionTypes.add(t);

			return this;
		}

		/**
		 * Bake an aspect.
		 * @param world
		 * @return Instance of Aspect.
		 */
		public Aspect build(World world) {
			ComponentTypeFactory tf = world.getComponentManager().typeFactory;
			Aspect aspect = new Aspect();
			associate(tf, allTypes, aspect.allSet);
			associate(tf, exclusionTypes, aspect.exclusionSet);
			associate(tf, oneTypes, aspect.oneSet);

			return aspect;
		}

		private static void associate(ComponentTypeFactory tf, Bag<Class<? extends Component>> types, BitVector componentBits) {
			for (Class<? extends Component> t : types) {
				componentBits.set(tf.getIndexFor(t));
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Builder builder = (Builder) o;

			if (!allTypes.equals(builder.allTypes))
				return false;
			if (!exclusionTypes.equals(builder.exclusionTypes))
				return false;
			if (!oneTypes.equals(builder.oneTypes))
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = allTypes.hashCode();
			result = 31 * result + exclusionTypes.hashCode();
			result = 31 * result + oneTypes.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "Aspect[" +
				"all=" + append(allTypes) +
				", one=" + append(oneTypes) +
				", exclude=" + append(exclusionTypes) +
				']';
		}

		private StringBuilder append(Bag<Class<? extends Component>> types) {
			StringBuilder sb = new StringBuilder();
			String delim = "";

			sb.append("(");
			for (Class<? extends Component> type : types) {
				sb.append(delim).append(type.getSimpleName());
				delim = ", ";
			}
			sb.append(")");
			return sb;
		}
	}
}
