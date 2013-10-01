package com.artemis;

import com.artemis.utils.Bag;

/**
 * Let's the user set the size.
 * Setting the size does not resize the bag, nor will it clean up contents
 * beyond the given size.
 * Only use this if you know what you are doing!
 *
 * @author junkdog
 * @param <T>
 */
class WildBag<T> extends Bag<T> {
	
	/**
	 * Set the size.
	 * This will not resize the bag, nor will it clean up contents beyond the
	 * given size. Use with caution.
	 *
	 * @param size
	 *		the size to set
	 */
	void setSize(int size) {
		this.size = size;
	}

}
