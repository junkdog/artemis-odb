package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Sticky;
import com.artemis.component.Asset;
import com.artemis.component.Cullible;
import com.artemis.component.Position;
import com.artemis.component.Size;
import com.artemis.component.Sprite;
import com.artemis.component.Velocity;

@Bind({Sprite.class, Cullible.class})
public interface ShipNoMethods extends EntityFactory<ShipNoMethods> {
}
