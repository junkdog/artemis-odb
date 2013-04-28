package com.artemis.utils;

import java.util.Iterator;

/**
 * Reusable iterator.
 */
public interface BagIterator<T> extends Iterator<T> {
	void reset();
}
