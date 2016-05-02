package com.artemis.weaver.template;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.link.LinkListener;
import com.artemis.link.MultiFieldMutator;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.Field;

import static com.artemis.Aspect.all;

public class MultiEntityLink extends Component {
	public Bag<Entity> field;

	public static class Mutator implements MultiFieldMutator<Bag<Entity>, MultiEntityLink> {
		private EntitySubscription all;

		@Override
		public void setWorld(World world) {
			all = world.getAspectSubscriptionManager().get(all());
		}

		@Override
		public void validate(int sourceId, Bag<Entity> entities, LinkListener listener) {
			for (int i = 0; entities.size() > i; i++) {
				Entity e = entities.get(i);
				if (!all.getActiveEntityIds().get(e.getId())) {
					entities.remove(i--);
					if (listener != null)
						listener.onTargetDead(sourceId, e.getId());
				}
			}
		}

		@Override
		public Bag<Entity> read(MultiEntityLink c, Field f) {
			return c.field;
		}
	}
}
