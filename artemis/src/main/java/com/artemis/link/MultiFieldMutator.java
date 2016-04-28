package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.reflect.Field;

interface MultiFieldMutator<T> {
	void validate(int sourceId, T collection, LinkListener listener);
	T read(Component c, Field f);
}
