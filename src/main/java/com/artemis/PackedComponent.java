package com.artemis;

public abstract class PackedComponent extends Component
{
	protected abstract PackedComponent setEntityId(int entityId);
	protected abstract void reset();
}
