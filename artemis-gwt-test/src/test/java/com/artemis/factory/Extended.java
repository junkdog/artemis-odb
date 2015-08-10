package com.artemis.factory;

import com.artemis.annotations.Bind;
import com.artemis.component.Sprite;

@Bind(Sprite.class)
public interface Extended extends ExhibitA {
	Extended sprite(String dummy); 
}
