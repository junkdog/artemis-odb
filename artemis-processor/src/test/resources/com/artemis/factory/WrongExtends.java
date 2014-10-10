package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.component.Velocity;

public interface WrongExtends extends EntityFactory<ExhibitA> {
	@Bind(Velocity.class) WrongExtends velocity(float x, float y);
}
