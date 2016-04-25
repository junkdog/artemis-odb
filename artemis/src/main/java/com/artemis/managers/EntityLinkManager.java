package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;


import static com.artemis.Aspect.all;
import static com.artemis.utils.reflect.ReflectionUtil.isGenericType;

/**
 * <p>Maintains relationships between entities. By default, all entities
 * are considered when intercepting relationships. To only consider a sub-set
 * of entities, this system can be fed a custom <code>aspect</code>.</p>
 *
 * <p>This system is optional and must be manually registered with
 * the world instance.</p>
 *
 * @see com.artemis.annotations.EntityId
 *
 */
public class EntityLinkManager extends BaseEntitySystem
		implements ComponentTypeFactory.ComponentTypeListener {

	private final Bag<LinkSite> singleLinkSites = new Bag<LinkSite>();
	private final Bag<Bag<LinkSite>> multiLinkSites = new Bag<Bag<LinkSite>>();

	private LinkFactory linkFactory;

	public EntityLinkManager(Aspect.Builder aspect) {
		super(aspect);
	}

	public EntityLinkManager() {
		this(all());
	}

	@Override
	protected void processSystem() {

	}

	@Override
	public void initialize(Bag<ComponentType> types) {
		linkFactory = new LinkFactory(world);
		for (int i = 0, s = types.size(); s > i; i++) {
			onCreated(types.get(i));
		}
	}

	@Override
	public void onCreated(ComponentType type) {
		Bag<LinkSite> links = linkFactory.create(type);
		if (links.isEmpty())
			return;

		if (links.size() == 1) {
			singleLinkSites.set(type.getIndex(), links.get(0));
		} else {
			Bag<LinkSite> bag = new Bag<LinkSite>(links.size());
			bag.addAll(links);
			multiLinkSites.set(type.getIndex(), bag);
		}

		links.clear();
	}

	static class LinkFactory {
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

		public Bag<LinkSite> create(ComponentType ct) {
			Class<?> type = ct.getType();
			Field[] fields = ClassReflection.getDeclaredFields(type);
			for (int i = 0; fields.length > i; i++) {
				Field f = fields[i];
				Class t = f.getType();
				int referenceTypeId = getReferenceType(f);
				if (referenceTypeId > 0) {
					if (SINGLE_REFERENCE == referenceTypeId) {
						links.add(new SingleLinkSite(world, ct, f, subscription(ct)));
					} else if (MULTI_REFERENCE == referenceTypeId) {
//						links.add(MultiLinkSite(world, ct, f, subscription(ct)));
						throw new UnsupportedOperationException("not impl");
					}
//					referencingFields.add(f);
//					referencingTypes.add(type);
//					referenced.add(new EntityReference(type, f));
				}
			}

			return links;
		}
	}

	abstract static class LinkSite implements EntitySubscription.SubscriptionListener {
		protected final ComponentType type;
		protected final Field field;
		protected final ComponentMapper<? extends Component> mapper;

		protected LinkSite(World world, ComponentType type, Field field, EntitySubscription subscription) {
			mapper = world.getMapper(type.getType());
			this.type = type;
			this.field = field;

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
	}

	static class SingleLinkSite extends LinkSite {

		private final IntBag sourceToTarget = new IntBag();
		private final IntBag targetToSource = new IntBag();
		private final EntityManager em;

		protected SingleLinkSite(World world,
		                         ComponentType type,
		                         Field field,
		                         EntitySubscription subscription) {

			super(world, type, field, subscription);
			em = world.getEntityManager();
		}


		@Override
		protected void check(int id) {
			// check target is alive
		}

		@Override
		protected void insert(int id) {
			Component component = mapper.get(id);
//			field.get(component) // can be int, entnity, bag, intbag
		}

		@Override
		protected void removed(int id) {

		}
	}

	interface FieldReader {
		int readField(Component c, Field f, IntBag out);
	}

	static class IntFieldReader implements FieldReader {
		@Override
		public int readField(Component c, Field f, IntBag out) {
			try {
				return (Integer) f.get(c);
			} catch (ReflectionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static class EntityFieldReader implements FieldReader {
		@Override
		public int readField(Component c, Field f, IntBag out) {
			try {
				Entity e = (Entity) f.get(c);
				return (e != null) ? e.getId() : -1;
			} catch (ReflectionException exc) {
				throw new RuntimeException(exc);
			}
		}
	}
}
