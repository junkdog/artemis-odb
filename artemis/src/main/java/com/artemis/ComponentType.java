package com.artemis;

import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;


/**
 * Identifies components in artemis without having to use classes.
 * <p>
 * Contains ordinal for a component type, which allows for fast
 * retrieval of components.
 *
 * @author Arni Arent
 * @author Adrian Papari
 */
public class ComponentType {
	enum Taxonomy {
		BASIC, POOLED, PACKED
	}

	/** The class type of the component type. */
	private final Class<? extends Component> type;
	private final Taxonomy taxonomy;
	
	boolean packedHasWorldConstructor = false;

	/** Ordinal for fast lookups. */
	private final int index;

	ComponentType(Class<? extends Component> type, int index) {
		
		this.index = index;
		this.type = type;
		if (ClassReflection.isAssignableFrom(PackedComponent.class, type)) {
			taxonomy = Taxonomy.PACKED;
			packedHasWorldConstructor = hasWorldConstructor(type);
		} else if (ClassReflection.isAssignableFrom(PooledComponent.class, type)) {
			taxonomy = Taxonomy.POOLED;
		} else {
			taxonomy = Taxonomy.BASIC;
		}
	}

	private static boolean hasWorldConstructor(Class<? extends Component> type) {
		Constructor[] constructors = ClassReflection.getConstructors(type);
		for (int i = 0; constructors.length > i; i++) {
			@SuppressWarnings("rawtypes")
			Class[] types = constructors[i].getParameterTypes();
			if (types.length == 1 && types[0] == World.class)
				return true;
		}
		
		return false;
	}

	/**
	 * Get the component type's index.
	 *
	 * Index is distinct for each {@link World} instance,
	 * allowing for fast lookups.
	 *
	 * @return the component types index
	 */
	public int getIndex() {
		return index;
	}
	
	protected Taxonomy getTaxonomy() {
		return taxonomy;
	}

	/**
	 * @return {@code true} if of taxonomy packed.
	 */
	public boolean isPackedComponent() {
		return taxonomy == Taxonomy.PACKED;
	}

	/**
	 * @return {@code Class} that this type represents.
	 */
	public Class<? extends Component> getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "ComponentType["+ ClassReflection.getSimpleName(type) +"] ("+index+")";
	}
}
