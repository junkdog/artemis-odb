package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;
import com.artemis.component.Asset;
import com.artemis.component.Cullible;
import com.artemis.component.HitPoints;
import com.artemis.component.Position;
import com.artemis.component.Size;
import com.artemis.component.Sprite;
import com.artemis.component.Velocity;

@Bind({Sprite.class, Cullible.class})
public interface Ship extends EntityFactory<Ship> {
	// method name maps Position
	@Bind(Position.class) Ship position(float x, float y);
	// parameter names must match field or setter name
	@Bind(Velocity.class) Ship velocity(float x, float y);
	@Bind(Velocity.class) Ship velocity(float x);
	@Bind(Asset.class) Ship asset(String path);
	@Bind(Size.class) Ship size(float width, float height);
	@Bind(HitPoints.class) @Sticky Ship hitPoints(int current);
	@Bind(Cullible.class) Ship culled(boolean culled);
}
