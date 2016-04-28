package com.artemis.link;

import com.artemis.ComponentType;
import com.artemis.World;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.reflect.Field;

class MultiLinkSite extends LinkSite {
	MultiFieldMutator fieldMutator;

	protected MultiLinkSite(World world,
	                        ComponentType type,
	                        Field field) {

		super(world, type, field, LinkPolicy.Policy.CHECK_SOURCE);
	}

	@Override
	protected void check(int id) {
		Object collection = fieldMutator.read(mapper.get(id), field);
		fieldMutator.validate(id, collection, listener);
	}

	@Override
	protected void insert(int id) {
		if (listener != null)
			listener.onLinkEstablished(id, -1);
	}

	@Override
	protected void removed(int id) {
		if (listener != null)
			listener.onLinkKilled(id, -1);
	}
}
