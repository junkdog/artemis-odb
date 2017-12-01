package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.utils.IntBag;

public class LttIntBag extends Component {
	@EntityId
	public IntBag ids = new IntBag();
}
