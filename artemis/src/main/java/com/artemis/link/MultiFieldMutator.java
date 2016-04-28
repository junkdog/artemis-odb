package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.IntBag;
import com.artemis.utils.reflect.Field;

interface MultiFieldMutator {
	void read(Component c, Field f, IntBag out);
}
