package com.artemis.reference;

import com.artemis.EntityFactory;
import com.artemis.ParamArchTest.Asset;
import com.artemis.ParamArchTest.HitPoints;
import com.artemis.ParamArchTest.Position;
import com.artemis.ParamArchTest.Size;
import com.artemis.ParamArchTest.Velocity;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;

@Bind({Sprite.class, Cullible.class})
public interface Ship extends EntityFactory<Ship> {
	// method name maps Position
	@Bind(Position.class) Ship position(float x, float y);
	// parameter names must match field or setter name
	@Bind(Velocity.class) Ship velocity(float x, float y);
	@Bind(Asset.class) Ship asset(String path);
	@Bind(Size.class) Ship size(float width, float height);
	@Bind(HitPoints.class)@Sticky Ship hitPoints(int current);
}
