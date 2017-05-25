package com.artemis.link;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.reflect.Field;

/**
 * <p>Internal interface. Used for reading/writing entity
 * fields pointing to a single entity.</p>
 */
public interface UniFieldMutator {
	int read(Component c, Field f);
	void write(int value, Component c, Field f);
	void setWorld(World world);
}
