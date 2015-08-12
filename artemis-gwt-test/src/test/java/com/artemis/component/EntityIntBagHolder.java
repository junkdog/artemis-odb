package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;

public class EntityIntBagHolder extends Component {
	@EntityId public IntBag entities = new IntBag();;
	public IntBag notEntities = new IntBag();
}
