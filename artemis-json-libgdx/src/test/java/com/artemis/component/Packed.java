package com.artemis.component;

import com.artemis.PackedComponent;

public class Packed extends PackedComponent
{
	public int entityId;

	@Override
	protected void forEntity(int e) {
		entityId = e;
	}

	@Override
	protected void reset() {}

	@Override
	protected void ensureCapacity(int id) {
		// TODO Auto-generated method stub
	}
}
