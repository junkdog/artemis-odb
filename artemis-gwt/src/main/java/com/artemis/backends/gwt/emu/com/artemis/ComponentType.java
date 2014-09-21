package com.artemis;

import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Constructor;


/**
 * Identifies components in artemis without having to use classes.
 * <p>
 * This class keeps a list of all generated component types for fast
 * retrieval.
 * </p>
 *
 * @author Arni Arent
 */
public class ComponentType {
	enum Taxonomy {
		BASIC, POOLED, PACKED;
	}

	
	/** The class type of the component type. */
	private final Class<? extends Component> type;
	/** True if component type is a {@link PackedComponent} */
	private final Taxonomy taxonomy;

	boolean packedHasWorldConstructor = false;

	private final int index;

	/**
	 * <b>Do not call this constructor!</b> This method is only public so that
	 * we play nice with GWT.
	 * 
	 *  @@see {@link ComponentType#getTypeFor(Class)}
	 */
	public ComponentType(Class<? extends Component> type, int index) {
		
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
	 * @return the component types index
	 */
	public int getIndex() {
		return index;
	}
	
	protected Taxonomy getTaxonomy() {
		return taxonomy;
	}
	
	public boolean isPackedComponent() {
		return taxonomy == Taxonomy.PACKED;
	}
	
	protected Class<? extends Component> getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "ComponentType["+ ClassReflection.getSimpleName(type) +"] ("+index+")";
	}
}
