package com.artemis.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

public class EntityReferencing extends Component {
	@EntityId public int id;
	@EntityId public IntBag ids;
	public Entity entity;
	public Bag<Entity> entiies;
}