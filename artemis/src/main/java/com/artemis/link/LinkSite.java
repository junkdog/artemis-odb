package com.artemis.link;

import com.artemis.*;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;

import java.util.BitSet;

import static com.artemis.Aspect.all;
import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS;

abstract class LinkSite implements EntitySubscription.SubscriptionListener {
	protected final ComponentType type;
	protected final Field field;
	protected final ComponentMapper<? extends Component> mapper;
	protected final EntitySubscription subscription;
	protected final LinkPolicy.Policy policy;
	protected final BitSet activeEntityIds;
	protected LinkListener listener;

	protected LinkSite(World world,
	                   ComponentType type,
	                   Field field,
	                   LinkPolicy.Policy defaultPolicy) {

		this.type = type;
		this.field = field;
		LinkPolicy.Policy policyOverride = LinkFactory.getPolicy(field);
		this.policy = (policyOverride != null) ? policyOverride : defaultPolicy;

		mapper = world.getMapper(type.getType());

		activeEntityIds = world.getAspectSubscriptionManager().get(all()).getActiveEntityIds();

		AspectSubscriptionManager subscriptions = world.getAspectSubscriptionManager();
		subscription = subscriptions.get(all(type.getType()));
		subscription.addSubscriptionListener(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LinkSite that = (LinkSite) o;

		return type.equals(that.type) && field.equals(that.field);
	}

	@Override
	public int hashCode() {
		return type.hashCode() ^ field.hashCode();
	}


	@Override
	public void inserted(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			insert(ids[i]);
		}
	}

	protected abstract void insert(int id);

	@Override
	public void removed(IntBag entities) {
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			removed(ids[i]);
		}
	}

	protected abstract void removed(int id);

	protected abstract void check(int id);

	protected void process() {
		if (CHECK_SOURCE_AND_TARGETS != policy)
			return;

		IntBag entities = subscription.getEntities();
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
				check(ids[i]);
		}
	}
}
