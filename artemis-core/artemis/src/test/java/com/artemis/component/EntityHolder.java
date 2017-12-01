package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class EntityHolder extends Component {

	@EntityId
	public int entityId;
}
