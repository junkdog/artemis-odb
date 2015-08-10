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

@Bind({Sprite.class, Cullible.class, Position.class, Velocity.class,
	Asset.class, Size.class, HitPoints.class})
public interface ShipShortWire extends EntityFactory<ShipShortWire> {
	// method name maps Position
	ShipShortWire position(float x, float y);
	// parameter names must match field or setter name
	ShipShortWire velocity(float x, float y);
	ShipShortWire asset(String path);
	ShipShortWire size(float width, float height);
	@Sticky ShipShortWire hitPoints(int current);
}
