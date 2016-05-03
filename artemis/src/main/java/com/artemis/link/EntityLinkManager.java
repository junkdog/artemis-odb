package com.artemis.link;

import com.artemis.*;
import com.artemis.utils.Bag;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.Field;
import com.artemis.utils.reflect.ReflectionException;


import static com.artemis.Aspect.all;

/**
 * <p>Maintains relationships between entities.</p>
 *
 * <p>This system is optional and must be manually registered with
 * the world instance.</p>
 *
 * @see com.artemis.annotations.EntityId
 *
 */
public class EntityLinkManager extends BaseEntitySystem {

	final Bag<LinkSite> linkSites = new Bag<LinkSite>();
	final Bag<LinkSite> decoratedLinkSites = new Bag<LinkSite>();

	private final boolean requireListener;

	/**
	 * @param processSitesEvenIfNoListener If true, only act on fields with an attached {@link LinkListener}.
	 */
	public EntityLinkManager(boolean processSitesEvenIfNoListener) {
		super(all());
		this.requireListener = !processSitesEvenIfNoListener;
	}

	/**
	 * Processes all fields, even if they don't have a {@link LinkListener}.
	 */
	public EntityLinkManager() {
		this(true);
	}

	@Override
	protected void initialize() {
		LinkCreateListener listener = new LinkCreateListener(this);
		world.getComponentManager().getTypeFactory().register(listener);
	}


	@Override
	protected void processSystem() {
		if (requireListener) {
			process(decoratedLinkSites);
		} else {
			process(linkSites);
		}
	}

	private void process(Bag<LinkSite> sites) {
		for (LinkSite ls : sites) {
			ls.process();
		}
	}

	/**
	 * <p>Injects and associates the listener with the component. This method
	 * is only recommended if only a single field references entities, or if all entity
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
			for (LinkSite site : linkSites) {
				if (ct.equals(site.type) && (f == null || site.field.equals(f))) {
					site.listener = listener;
					if (!decoratedLinkSites.contains(site))
						decoratedLinkSites.add(site);
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

			for (int i = 0, s = links.size(); s > i; i++) {
				elm.linkSites.add(links.get(i));
			}
		}
	}
}
