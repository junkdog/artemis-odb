package com.artemis.component;

import com.artemis.Entity;
import com.artemis.PackedComponent;

public class Packed extends PackedComponent
{
	public int entityId;

	@Override
	protected PackedComponent forEntity(Entity e) {
		entityId = e.getId();
		return this;
	}

	@Override
	protected void reset() {}
}
