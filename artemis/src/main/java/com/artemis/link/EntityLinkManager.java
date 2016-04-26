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
		processSingle();
//		processMulti();
	}

	private void processSingle() {
		for (LinkSite ls : singleLinkSites) {
			ls.process();
		}
	}

	public void register(Class<? extends Component> component, LinkListener listener) {
		ComponentType ct = world.getComponentManager().getTypeFactory().getTypeFor(component);
		for (LinkSite site : singleLinkSites) {
			if (ct.equals(site.type)) {
				site.listener = listener;
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
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		}
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

}
