package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.component.ExtPosition;
import com.artemis.component.Position;
import com.artemis.component.Velocity;

@Bind({Position.class, Velocity.class})
public interface FactoryA extends EntityFactory<FactoryA> {
	@Bind(ExtPosition.class) FactoryA extPos();
}
