package com.artemis.component.link;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.LinkPolicy;
import com.artemis.utils.IntBag;

import static com.artemis.annotations.LinkPolicy.Policy.CHECK_SOURCE_AND_TARGETS;

public class MultiLinkCheckAll extends Component {
	@EntityId
	@LinkPolicy(CHECK_SOURCE_AND_TARGETS)
	public IntBag other = new IntBag();
}
