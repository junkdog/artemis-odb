package com.artemis;

import java.util.BitSet;

import com.artemis.utils.Bag;

public class ArchetypeBuilder {
	private final Bag<Class<? extends Component>> classes;
	
	public ArchetypeBuilder(Archetype parent) {
		classes = new Bag<Class<? extends Component>>();
		for (int i = 0; parent.types.length > i; i++)
			classes.add(parent.types[i].getType());
	}
	
	public ArchetypeBuilder() {
		classes = new Bag<Class<? extends Component>>();
	}
	
	public ArchetypeBuilder add(Class<? extends Component> type) {
		classes.add(type);
		return this;
	}
	
	public ArchetypeBuilder remove(Class<? extends Component> type) {
		classes.remove(type);
		return this;
	}
	
	public Archetype build(World world) {
		ComponentType[] types = resolveTypes(world);
		EntityManager em = world.getEntityManager();
		return new Archetype(types, em.compositionIdentity(bitset(types)));
	}

	private static BitSet bitset(ComponentType[] types) {
		BitSet bs = new BitSet();
		for (int i = 0; types.length > i; i++)
			bs.set(types[i].getIndex());
			
		return bs;
	}

	private ComponentType[] resolveTypes(World world) {
		ComponentTypeFactory tf = world.getComponentManager().typeFactory;
		ComponentType[] types = new ComponentType[classes.size()];
		for (int i = 0, s = classes.size(); s > i; i++)
			types[i] = tf.getTypeFor(classes.get(i));
		
		return types;
	}
	
	public final static class Archetype {
		final ComponentType[] types;
		final int compositionId;
		
		public Archetype(ComponentType[] types, int compositionId) {
			this.types = types;
			this.compositionId = compositionId;
		}
	}
}