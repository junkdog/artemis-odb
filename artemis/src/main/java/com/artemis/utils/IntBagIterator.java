package com.artemis.utils;

import java.util.NoSuchElementException;

/**
 * Base iterator for {@link IntBag}
 * 
 * example use 
 * 
 * IntBag ids;
 * ....
 * final IntBagIterator it = new IntBagIterator(ids);
 * while (it.hasNext())
 *    doSomething(it.next());
 *
 * or
 * 
 * for (final IntIterator it = iterate(ids); it.hasNext();)
 *    doSomething(it.next());
 *  
 */
public class IntBagIterator {

	protected final IntBag intBag;
	protected int index;

	/**
	 * Create iterator for IntBag
	 * @return true if the iteration has more elements
	 */
	public IntBagIterator(final IntBag intBag) {
		this.intBag = intBag;
		index = -1;
	}
	
	/**
	 * Create iterator for IntBag
	 * @return true if the iteration has more elements
	 */
	public IntBagIterator(final IntBag intBag, final int indexBegin) {
		this.intBag = intBag;
		index = indexBegin - 1;
	}

	/**
	 * Returns true if the iteration has more elements. (In other words, returns true if next() would return an element rather than throwing an exception.)
	 * @return true if the iteration has more elements
	 */
	public boolean hasNext() {
		return intBag != null && index < intBag.size() - 1;
	}

	/**
	 * Returns the next element in the iteration.
	 * @return the next element in the iteration
	 */
	public int next() {
		if (hasNext()) {
			index++;
			return intBag.get(index);
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * Removes from the underlying collection the last element returned by this iterator (optional operation).
	 * This method can be called only once per call to next().
	 * The behavior of an iterator is unspecified if the underlying collection is modified while the iteration is in progress in any way other than by calling this method.
	 */
	public void remove() {
		
	}
	
}
