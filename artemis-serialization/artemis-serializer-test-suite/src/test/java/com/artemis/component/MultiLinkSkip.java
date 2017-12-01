package com.artemis.component;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.Bag;

import static com.artemis.annotations.LinkPolicy.Policy.SKIP;

public class MultiLinkSkip extends Component {
	@LinkPolicy(SKIP)
	public Bag<Entity> other = new Bag<Entity>();
}
