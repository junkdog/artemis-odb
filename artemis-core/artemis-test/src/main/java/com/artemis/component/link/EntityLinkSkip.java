package com.artemis.component.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.LinkPolicy;

import static com.artemis.annotations.LinkPolicy.Policy.SKIP;

public class EntityLinkSkip extends Component {
	@LinkPolicy(SKIP)
	public Entity other;
}
