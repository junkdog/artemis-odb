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

	public EntityLinkManager(Aspect.Builder aspect) {
		super(aspect);
	}

	public EntityLinkManager() {
		this(all());
	}

	@Override
	protected void initialize() {
		LinkCreateListener listener = new LinkCreateListener(this);
		world.getComponentManager().getTypeFactory().register(listener);
	}

	@Override
	protected void processSystem() {
		processUni();
		processMulti();
	}

	private void processUni() {
		for (LinkSite ls : singleLinkSites) {
			ls.process();
		}
	}

	private void processMulti() {
		for (Bag<LinkSite> ls : multiLinkSites) {
			for (int i = 0, s = ls.size(); s > i; i++) {
				ls.get(i).process();
			}
		}
	}

	public void register(Class<? extends Component> component, LinkListener listener) {
		ComponentType ct = world.getComponentManager().getTypeFactory().getTypeFor(component);
		for (LinkSite site : singleLinkSites) {
			if (ct.equals(site.type)) {
				site.listener = listener;
			}
		}
		for (Bag<LinkSite> ls : multiLinkSites) {
			for (int i = 0, s = ls.size(); s > i; i++) {
				if (ct.equals(ls.get(i).type)) {
					ls.get(i).process();
				}
			}
		}
	}

	public void register(Class<? extends Component> component, String field, LinkListener listener) {
		try {
			Field f = ClassReflection.getDeclaredField(component, field);
			ComponentType ct = world.getComponentManager().getTypeFactory().getTypeFor(component);
			for (LinkSite site : singleLinkSites) {
				if (ct.equals(site.type) && site.field.equals(f)) {
					site.listener = listener;
				}
			}
			for (Bag<LinkSite> ls : multiLinkSites) {
				for (int i = 0, s = ls.size(); s > i; i++) {
					LinkSite site = ls.get(i);
					if (ct.equals(site.type) && site.field.equals(f)) {
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
