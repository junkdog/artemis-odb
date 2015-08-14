package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import com.artemis.utils.Vector2;

@PooledWeaver
public class Anchor extends Component {
	public Vector2 anchor = new Vector2();
}
