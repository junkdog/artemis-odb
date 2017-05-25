package com.artemis.link;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.utils.reflect.Field;

/**
 * <p>Internal interface. Used for reading/writing entity
 * fields pointing to multiple entities.</p>
 */
public interface MultiFieldMutator<T, C extends Component> {
	void validate(int sourceId, T collection, LinkListener listener);
	T read(C c, Field f);
	void setWorld(World world);
}
