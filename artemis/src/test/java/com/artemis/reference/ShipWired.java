package com.artemis.reference;

import com.artemis.EntityFactory;
import com.artemis.ParamArchTest.Asset;
import com.artemis.ParamArchTest.HitPoints;
import com.artemis.ParamArchTest.Position;
import com.artemis.ParamArchTest.Size;
import com.artemis.ParamArchTest.Velocity;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;

@Bind({Sprite.class, Cullible.class, Position.class, Velocity.class, Asset.class, Size.class, HitPoints.class})
public interface ShipWired extends EntityFactory<ShipWired> {
	// method name maps Position
	ShipWired position(float x, float y);
	// parameter names must match field or setter name
	ShipWired velocity(float x, float y);
	ShipWired asset(String path);
	ShipWired size(float width, float height);
	@Sticky ShipWired hitPoints(int current);
}
