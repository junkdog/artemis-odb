package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;
import com.artemis.component.Asset;
import com.artemis.component.HitPoints;
import com.artemis.component.Position;
import com.artemis.component.Size;
import com.artemis.component.Velocity;

public interface TooManyRefs extends EntityFactory<TooManyRefs> {
	@Bind({Position.class, HitPoints.class})
	TooManyRefs hitPoints(int current);
}
