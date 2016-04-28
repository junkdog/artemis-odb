package com.artemis.link;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;


import static com.artemis.Aspect.all;

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
public class EntityLinkManager extends BaseEntitySystem {

	final Bag<LinkSite> singleLinkSites = new Bag<LinkSite>();
	final Bag<Bag<LinkSite>> multiLinkSites = new Bag<Bag<LinkSite>>();

	private final boolean requireListener;

	public EntityLinkManager(Aspect.Builder aspect, boolean requireListenerToProcess) {
		super(aspect);
		this.requireListener = requireListenerToProcess;
	}

	public EntityLinkManager() {
		this(all(), false);
	}

	@Override
	protected void initialize() {
		LinkCreateListener listener = new LinkCreateListener(this);
		world.getComponentManager().getTypeFactory().register(listener);
	}


	@Override
	protected void processSystem() {
		for (LinkSite ls : singleLinkSites) {
			if (requireListener && ls.listener == null)
				continue;

			ls.process();
		}

		for (Bag<LinkSite> sites : multiLinkSites) {
			for (int i = 0, s = sites.size(); s > i; i++) {
				LinkSite ls = sites.get(i);
				if (requireListener && ls.listener == null)
					continue;

				ls.process();
			}
		}
	}

	/**
	 * <p>Injects and associates the listener with the component. This method
	 * is only recommended if only field references entities, or if all entity
	 * fields are of the same type.</p>
	 *
	 * <p>Each <code>ComponentType::Field</code> pair can only have one {@link LinkListener}</p>
	 *
	 * @param component component type associated with listener
	 * @param listener link listener
	 */
	public void register(Class<? extends Component> component, LinkListener listener) {
		register(component, null, listener);
	}

	/**
	 * <p>Injects and associates the listener with a specific field for a given
	 * component type.</p>
	 *
	 * <p>Each <code>ComponentType::Field</code> pair can only have one {@link LinkListener}</p>
	 *
	 * @param component component type associated with listener
	 * @param field target field for listener
	 * @param listener link listener
	 */
	public void register(Class<? extends Component> component, String field, LinkListener listener) {
		world.inject(listener);
		try {
			Field f = (field != null)
				? ClassReflection.getDeclaredField(component, field)
				: null;

			ComponentType ct = world.getComponentManager().getTypeFactory().getTypeFor(component);
			for (LinkSite site : singleLinkSites) {
				if (ct.equals(site.type) && (f == null || site.field.equals(f))) {
					site.listener = listener;
				}
			}
			for (Bag<LinkSite> ls : multiLinkSites) {
				for (int i = 0, s = ls.size(); s > i; i++) {
					LinkSite site = ls.get(i);
					if (ct.equals(site.type) && (f == null || site.field.equals(f))) {
						ls.get(i).process();
					}
				}
			}
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static class LinkCreateListener implements ComponentTypeFactory.ComponentTypeListener {
		private final EntityLinkManager elm;
		private final LinkFactory linkFactory;

		public LinkCreateListener(EntityLinkManager elm) {
			this.elm = elm;
			this.linkFactory = new LinkFactory(elm.getWorld());
		}

		@Override
		public void initialize(Bag<ComponentType> types) {
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
				elm.singleLinkSites.add(links.get(0));
			} else {
				Bag<LinkSite> bag = new Bag<LinkSite>(links.size());
				bag.addAll(links);
				elm.multiLinkSites.add(bag);
			}
		}
	}
}
