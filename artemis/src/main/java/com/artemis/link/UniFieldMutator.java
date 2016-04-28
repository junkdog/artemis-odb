package com.artemis.link;

import com.artemis.Component;
import com.artemis.utils.reflect.Field;

interface UniFieldMutator {
	int read(Component c, Field f);
}
