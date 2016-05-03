package com.artemis.component.link;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class EntityLink extends Component {
	@EntityId
	public int otherId;
	public int nothingHere;
}
