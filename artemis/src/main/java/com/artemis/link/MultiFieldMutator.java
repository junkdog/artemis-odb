package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.reflect.Field;

interface MultiFieldMutator<T, C extends Component> {
	void validate(int sourceId, T collection, LinkListener listener);
	T read(C c, Field f);
}
