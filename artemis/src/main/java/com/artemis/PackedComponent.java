package com.artemis;

public abstract class PackedComponent extends Component {
	protected abstract PackedComponent forEntity(Entity e);
	protected abstract void reset();
}
