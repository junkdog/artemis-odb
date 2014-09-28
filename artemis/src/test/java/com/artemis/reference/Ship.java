package com.artemis.reference;

import com.artemis.EntityFactory;
import com.artemis.ParamArchTest.Asset;
import com.artemis.ParamArchTest.Position;
import com.artemis.ParamArchTest.Size;
import com.artemis.ParamArchTest.Velocity;
import com.artemis.annotations.CRef;
import com.artemis.annotations.Sticky;

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
