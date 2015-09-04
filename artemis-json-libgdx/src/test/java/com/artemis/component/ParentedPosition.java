package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.Vector2;

public class ParentedPosition extends Component {
	public Vector2 xy = new Vector2();
	@EntityId public int origin;
}
