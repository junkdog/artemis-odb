package com.artemis.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.LinkPolicy;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE;

public class EntityLinkC extends Component {
	@LinkPolicy(CHECK_SOURCE)
	public Entity other;
}
