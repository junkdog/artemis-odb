package com.artemis.weaver.template;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.link.UniFieldMutator;
import com.artemis.utils.reflect.Field;

public class UniEntityLink extends Component {
	public Entity field;

	public static class Mutator implements UniFieldMutator {
		private World world;

		@Override
		public int read(Component c, Field f) {
			Entity e = ((UniEntityLink) c).field;
			return (e != null) ? e.getId() : -1;
		}

		@Override
		public void write(int value, Component c, Field f) {
			Entity e = (value != -1) ? world.getEntity(value) : null;
			((UniEntityLink) c).field = e;
		}

		@Override
		public void setWorld(World world) {
			this.world = world;
		}
	}
}
