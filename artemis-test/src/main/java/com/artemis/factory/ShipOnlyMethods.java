package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.CRef;
import com.artemis.annotations.Sticky;
import com.artemis.component.Asset;
import com.artemis.component.HitPoints;
import com.artemis.component.Position;
import com.artemis.component.Size;
import com.artemis.component.Velocity;

public interface ShipOnlyMethods extends EntityFactory<ShipOnlyMethods> {
	// method name maps Position
	@CRef(Position.class) ShipOnlyMethods position(float x, float y);
	// parameter names must match field or setter name
	@CRef(Velocity.class) ShipOnlyMethods velocity(float x, float y);
	@CRef(Asset.class) ShipOnlyMethods asset(String path);
	@CRef(Size.class) ShipOnlyMethods size(float width, float height);
	@CRef(HitPoints.class) @Sticky ShipOnlyMethods hitPoints(int current);
}
