package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.reflect.Field;

interface MultiFieldMutator<T> {
	T read(Component c, Field f);
}
