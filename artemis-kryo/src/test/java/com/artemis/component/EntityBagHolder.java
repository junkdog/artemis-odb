package com.artemis.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;

public class EntityBagHolder extends Component {
	public Bag<Entity> entities = new Bag<Entity>();
}
