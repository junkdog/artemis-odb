package com.artemis.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 * 
 * @param <E>
 *		object type this bag holds
 *
 * @author Arni Arent
 */
public class Bag<E> implements ImmutableBag<E> {

	/** The backing array. */
	E[] data;
	/** The amount of elements contained in bag. */
	protected int size = 0;
	/** The iterator, it is only created once and reused when required. */
	private BagIterator it;


	/**
	 * Constructs an empty Bag with an initial capacity of 64.
	 */
	public Bag() {
		this(64);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 * 
	 * @param capacity
	 *			the initial capacity of Bag
	 */
	@SuppressWarnings("unchecked")
	public Bag(int capacity) {
		data = (E[])new Object[capacity];
	}


	/**
	 * Removes the element at the specified position in this Bag.
	 * <p>
	 * It does this by overwriting it was last element then removing last
	 * element
	 * </p>
	 * 
	 * @param index
	 *			the index of element to be removed
	 *
	 * @return element that was removed from the Bag
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public E remove(int index) throws ArrayIndexOutOfBoundsException {
		E e = data[index]; // make copy of element to remove so it can be returned
		data[index] = data[--size]; // overwrite item to remove with last element
		data[size] = null; // null last element, so gc can do its work
		return e;
	}

	/**
	 * Sorts the bag using the {@code comparator}.
	 *
	 * @param comparator
	 */
	public void sort(Comparator<E> comparator) {
		Sort.instance().sort(this, comparator);
	}
	
	/**
	 * Remove and return the last object in the bag.
	 * 
	 * @return the last object in the bag, null if empty
	 */
	public E removeLast() {
		if(size > 0) {
			E e = data[--size];
			data[size] = null;
			return e;
		}
		
		return null;
	}

	/**
	 * Removes the first occurrence of the specified element from this Bag, if
	 * it is present.
	 * <p>
	 * If the Bag does not contain the element, it is unchanged. It does this
	 * by overwriting it was last element then removing last element
	 * </p>
	 * 
	 * @param e
	 *			element to be removed from this list, if present
	 *
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean remove(E e) {
		for (int i = 0; i < size; i++) {
			E e2 = data[i];

			if (e == e2) {
				data[i] = data[--size]; // overwrite item to remove with last element
				data[size] = null; // null last element, so gc can do its work
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Check if bag contains this element.
	 * 
	 * @param e
	 *			element to check
	 *
	 * @return {@code true} if the bag contains this element
	 */
	@Override
	public boolean contains(E e) {
		for(int i = 0; size > i; i++) {
			if(e == data[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes from this Bag all of its elements that are contained in the
	 * specified Bag.
	 * 
	 * @param bag
	 *			Bag containing elements to be removed from this Bag
	 *
	 * @return {@code true} if this Bag changed as a result of the call
	 */
	public boolean removeAll(ImmutableBag<E> bag) {
		boolean modified = false;

		for (int i = 0, s = bag.size(); s > i; i++) {
			E e1 = bag.get(i);

			for (int j = 0; j < size; j++) {
				E e2 = data[j];

				if (e1 == e2) {
					remove(j);
					j--;
					modified = true;
					break;
				}
			}
		}

		return modified;
	}

	/**
	 * Returns the element at the specified position in Bag.
	 * 
	 * @param index
	 *			index of the element to return
	 *
	 * @return the element at the specified position in bag
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	@Override
	public E get(int index) throws ArrayIndexOutOfBoundsException {
		return data[index];
	}
	
	/**
	 * Returns the element at the specified position in Bag. This method
	 * ensures that the bag grows if the requested index is outside the bounds
	 * of the current backing array.
	 * 
	 * @param index
	 *			index of the element to return
	 *
	 * @return the element at the specified position in bag
	 *
	 */
	public E safeGet(int index) {
		if(index >= data.length) {
			grow((index * 7) / 4 + 1);
		}
		
		return data[index];
	}

	/**
	 * Returns the number of elements in this bag.
	 * 
	 * @return the number of elements in this bag
	 */
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * Returns the number of elements the bag can hold without growing.
	 * 
	 * @return the number of elements the bag can hold without growing
	 */
	public int getCapacity() {
		return data.length;
	}
	
	/**
	 * Checks if the internal storage supports this index.
	 * 
	 * @param index
	 *			index to check
	 *
	 * @return {@code true} if the index is within bounds
	 */
	public boolean isIndexWithinBounds(int index) {
		return index < getCapacity();
	}

	/**
	 * Returns true if this bag contains no elements.
	 * 
	 * @return {@code true} if this bag contains no elements
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Adds the specified element to the end of this bag.
	 * <p>
	 * If required, it also increases the capacity of the bag.
	 * </p>
	 * 
	 * @param e
	 *			element to be added to this list
	 */
	public void add(E e) {
		// is size greater than capacity increase capacity
		if (size == data.length) {
			grow();
		}

		data[size++] = e;
	}

	/**
	 * Set element at specified index in the bag.
	 * 
	 * @param index
	 *			position of element
	 * @param e
	 *			the element
	 */
	public void set(int index, E e) {
		if(index >= data.length) {
			grow((index * 7) / 4 + 1);
		}
		size = Math.max(size, index + 1);
		data[index] = e;
	}

	/**
	 * Increase the capacity of the bag.
	 * <p>
	 * Capacity will increase by (3/2)*capacity + 1.
	 * </p>
	 */
	private void grow() {
		int newCapacity = (data.length * 7) / 4 + 1;
		grow(newCapacity);
	}

	/**
	 * Increase the capacity of the bag.
	 *
	 * @param newCapacity
	 *			new capacity to grow to
	 *
	 * @throws ArrayIndexOutOfBoundsException if new capacity is smaller than old
	 */
	@SuppressWarnings("unchecked")
	private void grow(int newCapacity) throws ArrayIndexOutOfBoundsException {
		E[] oldData = data;
		data = (E[])new Object[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}

	/**
	 * Check if an item, if added at the given item will fit into the bag.
	 * <p>
	 * If not, the bag capacity will be increased to hold an item at the index.
	 * </p>
	 *
	 * @param index
	 *			index to check
	 */
	public void ensureCapacity(int index) {
		if(index >= data.length) {
			grow(index);
		}
	}

	/**
	 * Removes all of the elements from this bag.
	 * <p>
	 * The bag will be empty after this call returns.
	 * </p>
	 */
	public void clear() {
		Arrays.fill(data, 0, size, null);
		size = 0;
	}
	
	/**
	 * Removes all of the elements from this by re-allocating the backing
	 * array.
	 * <p>
	 * The bag will be empty after this call returns.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public void fastClear() {
		// new null array so gc can clean up old one
		data = (E[])new Object[data.length];
		size = 0;
	}

	/**
	 * Add all items into this bag.
	 *
	 * @param items
	 *			bag with items to add
	 */
	public void addAll(ImmutableBag<E> items) {
		for(int i = 0, s = items.size(); s > i; i++) {
			add(items.get(i));
		}
	}
	
	/**
	 * Returns this bag's underlying array.
	 * <p>
	 * Use with care.
	 * </p>
	 * 
	 * @return the underlying array
	 *
	 * @see Bag#size()
	 */
	public Object[] getData() {
		return data;
	}

	@Override
	public Iterator<E> iterator() {
		if (it == null) it = new BagIterator();

		it.validCursorPos = false;
		it.cursor = 0;
		
		return it;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bag(");
		for (int i = 0; size > i; i++) {
			if (i > 0) sb.append(", ");
			sb.append(data[i]);
		}
		sb.append(')');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Bag bag = (Bag) o;
		return size == bag.size() && Arrays.equals(data, bag.data);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0, s = size; s > i; i++) {
			hash = (127 * hash) + data[i].hashCode();
		}

		return hash;
	}

	/**
	 * An Iterator for Bag.
	 *
	 * @see java.util.Iterator
	 */
	private final class BagIterator implements Iterator<E> {

		/** Current position. */
		private int cursor;
		/** True if the current position is within bounds. */
		private boolean validCursorPos;


		@Override
		public boolean hasNext() {
			return (cursor < size);
		}


		@Override
		public E next() throws NoSuchElementException {
			if (cursor == size) {
				throw new NoSuchElementException("Iterated past last element");
			}

			E e = data[cursor++];
			validCursorPos = true;
			return e;
		}

		
		@Override
		public void remove() throws IllegalStateException {
			if (!validCursorPos) {
				throw new IllegalStateException();
			}
			
			validCursorPos = false;
			Bag.this.remove(--cursor);
		}
	}
}
