package com.artemis.weaver.template;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.link.UniFieldMutator;
import com.artemis.utils.reflect.Field;

public class UniEntityIdLink2 extends Component {
	@EntityId public int field;
	@EntityId public int field2;

	public static class Mutator implements UniFieldMutator {
		@Override
		public int read(Component c, Field f) {
			return ((UniEntityIdLink2) c).field;
		}

		@Override
		public void write(int value, Component c, Field f) {
			((UniEntityIdLink2) c).field = value;
		}

		@Override
		public void setWorld(World world) {}
	}

	public static class Mutator2 implements UniFieldMutator {
		@Override
		public int read(Component c, Field f) {
			return ((UniEntityIdLink2) c).field;
		}

		@Override
		public void write(int value, Component c, Field f) {
			((UniEntityIdLink2) c).field = value;
		}

		@Override
		public void setWorld(World world) {}
	}
}
