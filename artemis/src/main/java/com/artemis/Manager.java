package com.artemis;

import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Method;
import com.artemis.utils.reflect.ReflectionException;
import com.artemis.utils.reflect.ReflectionUtil;

import java.security.acl.Owner;

import static com.artemis.Aspect.all;
import static com.artemis.utils.reflect.ReflectionUtil.implementsObserver;


/**
 * A manager for handling entities in the world.
 * 
 * @author Arni Arent
 * @author Daan van Yperen
 */
public abstract class Manager extends BaseSystem {
	private int methodFlags;

	private static final int INSERTED = 1;
	private static final int REMOVED = 1 << 1;

	public void added(Entity e) {
		throw new RuntimeException("no,no,no");
	}

	public void deleted(Entity e) {
		throw new RuntimeException("no,no,no");
	}

	@Override
	protected void setWorld(World world) {
		super.setWorld(world);
		if(implementsObserver(this, "added"))
			methodFlags |= INSERTED;
		if(implementsObserver(this, "deleted"))
			methodFlags |= REMOVED;
	}

	protected void registerManager() {
		world.getAspectSubscriptionManager()
				.get(all())
				.addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
					@Override
					public void inserted(IntBag entities) {
						added(entities);
					}

					@Override
					public void removed(IntBag entities) {
						deleted(entities);
					}
				});
	}

	private void added(IntBag entities) {
		if ((methodFlags & INSERTED) == 0)
			return;

		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			added(world.getEntity(ids[i]));
		}
	}

	private void deleted(IntBag entities) {
		if ((methodFlags & REMOVED) == 0)
			return;

		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
			deleted(world.getEntity(ids[i]));
		}
	}

	@Override
	protected final void processSystem() {}
}
