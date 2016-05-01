package com.artemis.weaver.template;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.link.UniFieldMutator;
import com.artemis.utils.reflect.Field;

public class UniEntityLink extends Component {
	public Entity $field;

	public static class $fieldMutator implements UniFieldMutator {
		private World world;

		public $fieldMutator(World world) {
			this.world = world;
		}

		@Override
		public int read(Component c, Field f) {
			return ((UniEntityLink) c).$field.getId();
		}

		@Override
		public void write(int value, Component c, Field f) {
			((UniEntityLink) c).$field = world.getEntity(value);
		}

		@Override
		public void setWorld(World world) {
			this.world = world;
		}
	}
}
