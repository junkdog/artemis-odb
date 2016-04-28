package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Annotation;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE;
import static com.artemis.annotations.LinkPolicy.Policy.SKIP;
import static com.artemis.utils.reflect.ReflectionUtil.isGenericType;

class LinkFactory {
	private static final int NULL_REFERENCE = 0;
	private static final int SINGLE_REFERENCE = 1;
	private static final int MULTI_REFERENCE = 2;

	private final Bag<LinkSite> links = new Bag<LinkSite>();
	private World world;

	private final DefaultMutators defaultMutators = new DefaultMutators();

	public LinkFactory(World world) {
		this.world = world;
	}

	static int getReferenceTypeId(Field f) {
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

		links.clear();
		for (int i = 0; fields.length > i; i++) {
			Field f = fields[i];
			int referenceTypeId = getReferenceTypeId(f);
			if (referenceTypeId > 0 && (SKIP != getPolicy(f, CHECK_SOURCE))) {
				if (SINGLE_REFERENCE == referenceTypeId) {
					UniLinkSite linkSite = new UniLinkSite(world, ct, f);
					links.add(withDefaultMutator(linkSite));
				} else if (MULTI_REFERENCE == referenceTypeId) {
//						links.add(MultiLinkSite(world, ct, f, subscription(ct)));
					throw new UnsupportedOperationException("not impl");
				}
			}
		}

		return links;
	}

	private UniLinkSite withDefaultMutator(UniLinkSite linkSite) {
		Class type = linkSite.field.getType();
		if (Entity.class == type) {
			linkSite.fieldMutator = defaultMutators.entityFieldMutator;
		} else if (int.class == type) {
			linkSite.fieldMutator = defaultMutators.intFieldMutator;
		} else if (IntBag.class == type) {
//			linkSite.fieldMutator = defaultMutators.intBagFieldMutator;
		} else if (Bag.class == type) {
//			linkSite.fieldMutator = defaultReaders.entityBagFieldMutator;
		} else {
			throw new RuntimeException("unexpected '" + type + "', on " + linkSite.type);
		}

		return linkSite;
	}

	static LinkPolicy.Policy getPolicy(Field f, LinkPolicy.Policy defaultPolicy) {
		Annotation annotation = f.getDeclaredAnnotation(LinkPolicy.class);
		if (annotation != null) {
			LinkPolicy lp = annotation.getAnnotation(LinkPolicy.class);
			return lp.value();
		}

		return defaultPolicy;
	}

	class DefaultMutators {
		EntityFieldMutator entityFieldMutator = new EntityFieldMutator(world);
		IntFieldMutator intFieldMutator = new IntFieldMutator();
		IntBagFieldMutator intBagFieldMutator = new IntBagFieldMutator();
		EntityBagFieldMutator entityBagFieldMutator = new EntityBagFieldMutator();
	}
}
