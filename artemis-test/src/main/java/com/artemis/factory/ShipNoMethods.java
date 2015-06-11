package com.artemis.factory;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.component.Cullible;
import com.artemis.component.Sprite;

@Bind({Sprite.class, Cullible.class})
public interface ShipNoMethods extends EntityFactory<ShipNoMethods> {
}
