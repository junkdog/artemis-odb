package com.artemis.components;

import com.artemis.PooledComponent;
import com.artemis.annotations.Transient;

/**
 * Creates a tag, local to an instance of {@link com.artemis.io.SaveFileFormat}.
 *
 * @see com.artemis.io.SaveFileFormat#get(String)
 * @see com.artemis.io.SaveFileFormat#has(String)
 */
@Transient
public class SerializationTag extends PooledComponent {
	public String tag;

	@Override
	protected void reset() {
		tag = null;
	}
}
