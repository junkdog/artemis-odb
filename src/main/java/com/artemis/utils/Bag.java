package com.artemis.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 * 
 * @param <E>
 */
public class Bag<E> implements ImmutableBag<E> {

	/**
	 * The backing array.
	 */
	E[] data;

	/**
	 * The elements contained in bag.
	 */
	private int size = 0;

	/**
	 * The iterator. It is only created once and reused when required.
	 */
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
	 * Removes the element at the specified position in this Bag. does this by
	 * overwriting it was last element then removing last element
	 * 
	 * @param index
	 *			the index of element to be removed
	 * @return element that was removed from the Bag
	 */
	public E remove(int index) {
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
	 * @return the last object in the bag, null if empty.
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
	 * it is present. If the Bag does not contain the element, it is unchanged.
	 * does this by overwriting it was last element then removing last element
	 * 
	 * @param e
	 *			element to be removed from this list, if present
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
	 * @return the element at the specified position in bag
	 */
	@Override
	public E get(int index) {
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
	 * @return the number of elements the bag can hold without growing.
	 */
	public int getCapacity() {
		return data.length;
	}
	
	/**
	 * Checks if the internal storage supports this index.
	 * 
	 * @param index
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
	 * Adds the specified element to the end of this bag. if needed also
	 * increases the capacity of the bag.
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
			grow(index * 2);
		}
		size = Math.max(size, index + 1);
		data[index] = e;
	}

	/**
	 * Increase the capacity of the bag.
	 * Capacity will increase by (3/2)*capacity + 1.
	 */
	private void grow() {
		int newCapacity = (data.length * 3) / 2 + 1;
		grow(newCapacity);
	}

	/**
	 * Set the capacity of the bag.
	 * If the new capacity is smaller than the current, an
	 * {@link IndexOutOfBoundsException IndexOutOfBoundsException} is thrown.
	 *
	 * @param newCapacity
	 */
	@SuppressWarnings("unchecked")
	private void grow(int newCapacity) {
		E[] oldData = data;
		data = (E[])new Object[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}

	/**
	 * Check if an item, if added at the given item will fit into the bag.
	 * If not, the bag capacity will be increased to hold an item at the index.
	 *
	 * @param index
	 */
	public void ensureCapacity(int index) {
		if(index >= data.length) {
			grow(index*2);
		}
	}

	/**
	 * Removes all of the elements from this bag. The bag will be empty after
	 * this call returns.
	 */
	@SuppressWarnings("unchecked")
	public void clear() {
		// new null array so gc can clean up old one
		data = (E[])new Object[data.length];
		size = 0;
	}

	/**
	 * Add all items into this bag.
	 *
	 * @param items
	 */
	public void addAll(ImmutableBag<E> items) {
		for(int i = 0, s = items.size(); s > i; i++) {
			add(items.get(i));
		}
	}
	
	/**
	 * Returns this bag's underlying array. Use with care. 
	 * 
	 * @see Bag#size()
	 * @return the underlying array.
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


	/**
	 * An Iterator for Bag.
	 *
	 * @see Iterator
	 */
	private final class BagIterator implements Iterator<E> {
		private int cursor;
		private boolean validCursorPos;

		@Override
		public boolean hasNext() {
			return (cursor < size);
		}

		@Override
		public E next() {
			if (cursor == size) {
				throw new NoSuchElementException("Iterated past last element");
			}

			E e = data[cursor++];
			validCursorPos = true;
			return e;
		}

		@Override
		public void remove() {
			if (!validCursorPos) {
				throw new IllegalStateException();
			}
			
			validCursorPos = false;
			Bag.this.remove(--cursor);
		}
	}
}
