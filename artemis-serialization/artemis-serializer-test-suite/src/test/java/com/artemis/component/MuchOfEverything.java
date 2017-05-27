package com.artemis.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

public class MuchOfEverything extends Component {
	@EntityId
	public IntBag intIds = new IntBag();
	@EntityId public int otherId;
	public Entity e;
	public Bag<Entity> entities = new Bag<Entity>();
	public int notMe;
}
