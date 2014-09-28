package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.CRef;
import com.artemis.annotations.Sticky;
import com.artemis.component.Asset;
import com.artemis.component.Cullible;
import com.artemis.component.Position;
import com.artemis.component.Size;
import com.artemis.component.Sprite;
import com.artemis.component.Velocity;

@CRef({Sprite.class, Cullible.class})
public interface Ship extends EntityFactory<Ship> {
	// method name maps Position
	@CRef(Position.class) Ship position(float x, float y);
	// parameter names must match field or setter name
	@CRef(Velocity.class) Ship velocity(float x, float y);
	@CRef(Asset.class) Ship asset(String path);
	@CRef(Size.class) Ship size(float width, float height);
	@Sticky Ship hitPoints(int current);
}
