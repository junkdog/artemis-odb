package com.artemis.component.link;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;

public class MultiLinkSkipTargetCheck extends Component {
	public Bag<Entity> other = new Bag<Entity>();
}
