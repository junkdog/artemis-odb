package com.artemis.weaver.template;

import com.artemis.AbstractEntityWorld;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.link.UniFieldMutator;
import com.artemis.utils.reflect.Field;

public class UniEntityLink extends Component {
	public Entity field;

	public static class Mutator<T extends Entity> implements UniFieldMutator {
		private AbstractEntityWorld<T> world;

		@Override
		@SuppressWarnings("unchecked")
		public int read(Component c, Field f) {
			T e = (T) ((UniEntityLink) c).field;
			return (e != null) ? e.getId() : -1;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void write(int value, Component c, Field f) {
			T e = (value != -1) ? world.getEntity(value) : null;
			((UniEntityLink) c).field = e;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void setWorld(World world) {
			this.world = (AbstractEntityWorld<T>)world;
		}
	}
}
