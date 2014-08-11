package com.artemis.component;

import com.artemis.Entity;
import com.artemis.PackedComponent;

public class Packed extends PackedComponent
{
	public int entityId;

	@Override
	protected void forEntity(Entity e) {
		entityId = e.getId();
//		return this;
	}

	@Override
	protected void reset() {}

	@Override
	protected void ensureCapacity(int id) {
		// TODO Auto-generated method stub
		
	}
}
