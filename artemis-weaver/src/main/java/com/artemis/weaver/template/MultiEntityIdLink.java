package com.artemis.weaver.template;

import com.artemis.Component;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.link.LinkListener;
import com.artemis.link.MultiFieldMutator;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;

import static com.artemis.Aspect.all;

public class MultiEntityIdLink extends Component {
	@EntityId public IntBag field;

	public static class Mutator implements MultiFieldMutator<IntBag, MultiEntityIdLink> {
		private EntitySubscription all;

		@Override
		public void setWorld(World world) {
			all = world.getAspectSubscriptionManager().get(all());
		}

		@Override
		public void validate(int sourceId, IntBag ids, LinkListener listener) {
			for (int i = 0; ids.size() > i; i++) {
				int id = ids.get(i);
				if (!all.getActiveEntityIds().get(id)) {
					ids.remove(i--);
					if (listener != null)
						listener.onTargetDead(sourceId, id);
				}
			}
		}

		@Override
		public IntBag read(MultiEntityIdLink c, Field f) {
			return c.field;
		}
	}
}
