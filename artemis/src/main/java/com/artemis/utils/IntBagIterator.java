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
 * for (final IntBagIterator it = new IntBagIterator(ids); it.hasNext();)
 *    doSomething(it.next());
 *  
 */
public class IntBagIterator {

    protected static enum State {
        INIT,
        NEXT_CALLED,
        REMOVE_CALLED,
    }

	protected final IntBag intBag;
	protected int index;
	protected int size;
	protected State state;

	/**
	 * Create iterator for IntBag
	 * @return true if the iteration has more elements
	 */
	public IntBagIterator(final IntBag intBag) {
		this.intBag = intBag;
		this.index = -1;
		this.size = intBag != null ? intBag.size() : 0;
		assert this.size >= 0;
		this.state = State.INIT;
	}
	
	/**
	 * Create iterator for IntBag
	 * @return true if the iteration has more elements
	 */
	public IntBagIterator(final IntBag intBag, final int indexBegin) {
		this.intBag = intBag;
		assert indexBegin >= 0;
		this.index = indexBegin - 1;
		this.size = intBag != null ? intBag.size() : 0;
		assert this.size >= 0;
		this.state = State.INIT;
	}
	
	/**
	 * Create iterator for IntBag
	 * @return true if the iteration has more elements
	 */
	public IntBagIterator(final IntBag intBag, final int indexBegin, final int size) {
		this.intBag = intBag;
		assert indexBegin >= 0;
		this.index = indexBegin - 1;
		this.size = intBag != null ? (size < intBag.size() ? size : intBag.size()) : 0;
		assert this.size >= 0;
		this.state = State.INIT;
	}
	
	/**
	 * return current index
	 * This method can be called only per call to next().
	 * This method can not be called after call remove().
	 * 
	 * @throws IllegalStateException - if the next() method has not yet been called
	 */
	public int getCurrentIndex() {
		if (this.state == State.NEXT_CALLED && index < size && size <= intBag.size()) {
			return index;
		}
		throw new IllegalStateException();
	}

	/**
	 * Returns true if the iteration has more elements. (In other words, returns true if next() would return an element rather than throwing an exception.)
	 * @return true if the iteration has more elements
	 */
	public boolean hasNext() {
		return ((state != State.INIT && index < size - 1) || (state == State.INIT && index < size)) && size <= intBag.size();
	}

	/**
	 * Returns the next element in the iteration.
	 * @return the next element in the iteration
	 */
	public int next() {
		if (hasNext()) {
			index++;
			this.state = State.NEXT_CALLED;
			return intBag.get(index);
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * Removes from the underlying collection the last element returned by this iterator (optional operation).
	 * This method can be called only once per call to next().
	 * The behavior of an iterator is unspecified if the underlying collection is modified while the iteration is in progress in any way other than by calling this method.
	 * 
	 * @throws IllegalStateException - if the next() method has not yet been called, or the remove() method has already been called after the last call to the next() method
	 */
	public void remove() {
		if (this.state == State.NEXT_CALLED && index < size && size <= intBag.size()) {
			intBag.remove(index);
			index--;
			size--;
			assert index < size;
		}
		throw new IllegalStateException();
	}
	
}
