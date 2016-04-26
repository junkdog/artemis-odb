package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;

import static com.artemis.Aspect.all;
import static com.artemis.utils.reflect.ReflectionUtil.isGenericType;

class LinkFactory {
	private static final int NULL_REFERENCE = 0;
	private static final int SINGLE_REFERENCE = 1;
	private static final int MULTI_REFERENCE = 2;

	private final Bag<LinkSite> links = new Bag<LinkSite>();
	private World world;

	public LinkFactory(World world) {
		this.world = world;
	}


	private EntitySubscription subscription(ComponentType ct) {
		return world.getAspectSubscriptionManager()
			.get(all(ct.getType()));
	}

	static int getReferenceType(Field f) {
		Class type = f.getType();
		if (Entity.class == type)
			return SINGLE_REFERENCE;
		if (isGenericType(f, Bag.class, Entity.class))
			return MULTI_REFERENCE;

		boolean explicitEntityId = f.getDeclaredAnnotation(EntityId.class) != null;
		if (int.class == type && explicitEntityId)
			return SINGLE_REFERENCE;
		if (IntBag.class == type && explicitEntityId)
			return MULTI_REFERENCE;

		return NULL_REFERENCE;
	}

	Bag<LinkSite> create(ComponentType ct) {
		Class<?> type = ct.getType();
		Field[] fields = ClassReflection.getDeclaredFields(type);
		for (int i = 0; fields.length > i; i++) {
			Field f = fields[i];
			Class t = f.getType();
			int referenceTypeId = getReferenceType(f);
			if (referenceTypeId > 0) {
				if (SINGLE_REFERENCE == referenceTypeId) {
					SingleLinkSite linkSite = new SingleLinkSite(world, ct, f, subscription(ct));
					linkSite = withDefaultEntityReader(linkSite);
					links.add(linkSite);
				} else if (MULTI_REFERENCE == referenceTypeId) {
//						links.add(MultiLinkSite(world, ct, f, subscription(ct)));
					throw new UnsupportedOperationException("not impl");
				}
			}
		}

		return links;
	}

	private SingleLinkSite withDefaultEntityReader(SingleLinkSite linkSite) {
		Class type = linkSite.field.getType();
		if (type == Entity.class) {
			linkSite.entityReader = new EntityFieldReader();
		} else if (type == int.class) {
			linkSite.entityReader = new IntFieldReader();
		} else {
			throw new RuntimeException("unexpected '" + type + "', on " + linkSite.type);
		}

		return linkSite;
	}
}
