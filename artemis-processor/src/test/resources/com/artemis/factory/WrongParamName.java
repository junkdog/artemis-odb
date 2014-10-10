package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.component.Velocity;

public interface WrongParamName extends EntityFactory<WrongParamName> {
	@Bind(Velocity.class) WrongParamName velocity(float xy);
}
