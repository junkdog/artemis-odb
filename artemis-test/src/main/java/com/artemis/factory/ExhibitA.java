package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.UseSetter;
import com.artemis.annotations.Sticky;
import com.artemis.component.Asset;
import com.artemis.component.Complex;
import com.artemis.component.Cullible;
import com.artemis.component.HitPoints;
import com.artemis.component.Position;
import com.artemis.component.Sprite;
import com.artemis.component.Velocity;

@Bind({Position.class, Velocity.class, Sprite.class, Cullible.class,
	Asset.class, HitPoints.class})
public interface ExhibitA extends EntityFactory<ExhibitA> {
	// method name maps Position
	ExhibitA position(float x, float y);
	
	// overloaded methods
	ExhibitA velocity(float x, float y);
	ExhibitA velocity(float x);
	
	// strings
	ExhibitA asset(String path);
	
	// sticky
	@Sticky ExhibitA hitPoints(int current);
	
	// aliasing
	@Bind(Cullible.class) ExhibitA culled(boolean culled);
	
	// setter
	@Bind(Complex.class) @UseSetter ExhibitA pos(float x, float y);
	
	// setter, aliased
	@Bind(Complex.class) @UseSetter("vel") ExhibitA hoho(float x, float y);
}
