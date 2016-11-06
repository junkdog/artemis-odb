package com.artemis.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.EntityId;

public class EntityHolder extends Component {
	public Entity entity;

	@EntityId
	public int entityId;
}
