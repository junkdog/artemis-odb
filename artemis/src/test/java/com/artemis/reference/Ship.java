package com.artemis.reference;

import com.artemis.EntityFactory;
import com.artemis.annotations.Sticky;

public interface Ship extends EntityFactory<Ship> {
	// method name maps Position
	Ship position(float x, float y);
	// parameter names must match field or setter name
	Ship velocity(float x, float y);
	Ship asset(String path);
	Ship size(float width, float height);
	@Sticky Ship hitPoints(int current);
}
