package com.artemis.utils;

import com.artemis.Entity;

/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 */

public class Bag<E> implements ImmutableBag<E> {
	private Object[] data;
	private int size = 0;

	/**
	 * Constructs an empty Bag with an initial capacity of 64.
	 * 
	 */
	public Bag() {
		this(16);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 * 
	 * @param capacity
	 *            the initial capacity of Bag
	 */
	public Bag(int capacity) {
		data = new Object[capacity];
	}

	/**
	 * Removes the element at the specified position in this Bag. does this by
	 * overwriting it was last element then removing last element
	 * 
	 * @param index
	 *            the index of element to be removed
	 * @return element that was removed from the Bag
	 */
	public E remove(int index) {
		Object o = data[index]; // make copy of element to remove so it can be
		// returned
		data[index] = data[--size]; // overwrite item to remove with last
		// element
		data[size] = null; // null last element, so gc can do its work
		return (E) o;
	}
	
	
	/**
	 * Remove and return the last object in the bag.
	 * 
	 * @return the last object in the bag, null if empty.
	 */
	public E removeLast() {
		if(size > 0) {
			Object o = data[--size];
			data[size] = null;
			return (E) o;
		}
		
		return null;
	}

	/**
	 * Removes the first occurrence of the specified element from this Bag, if
	 * it is present. If the Bag does not contain the element, it is unchanged.
	 * does this by overwriting it was last element then removing last element
	 * 
	 * @param o
	 *            element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(E o) {
		for (int i = 0; i < size; i++) {
			Object o1 = data[i];

			if (o == o1) {
				data[i] = data[--size]; // overwrite item to remove with last
				// element
				data[size] = null; // null last element, so gc can do its work
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Check if bag contains this element.
	 * 
	 * @param o
	 * @return
	 */
	public boolean contains(E o) {
		for(int i = 0; size > i; i++) {
			if(o == data[i]) {
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
	 *            Bag containing elements to be removed from this Bag
	 * @return {@code true} if this Bag changed as a result of the call
	 */
	public boolean removeAll(Bag<E> bag) {
		boolean modified = false;

		for (int i = 0; i < bag.size(); i++) {
			Object o1 = bag.get(i);

			for (int j = 0; j < size; j++) {
				Object o2 = data[j];

				if (o1 == o2) {
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
	 *            index of the element to return
	 * @return the element at the specified position in bag
	 */
	public E get(int index) {
		return (E) data[index];
	}

	/**
	 * Returns the number of elements in this bag.
	 * 
	 * @return the number of elements in this bag
	 */
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
	 * Returns true if this list contains no elements.
	 * 
	 * @return true if this list contains no elements
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Adds the specified element to the end of this bag. if needed also
	 * increases the capacity of the bag.
	 * 
	 * @param o
	 *            element to be added to this list
	 */
	public void add(E o) {
		// is size greater than capacity increase capacity
		if (size == data.length) {
			grow();
		}

		data[size++] = o;
	}

	/**
	 * Set element at specified index in the bag.
	 * 
	 * @param index position of element
	 * @param o the element
	 */
	public void set(int index, E o) {
		if(index >= data.length) {
			grow(index*2);
		}
		size = index+1;
		data[index] = o;
	}

	private void grow() {
		int newCapacity = (data.length * 3) / 2 + 1;
		grow(newCapacity);
	}
	
	private void grow(int newCapacity) {
		Object[] oldData = data;
		data = new Object[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}

	/**
	 * Removes all of the elements from this bag. The bag will be empty after
	 * this call returns.
	 */
	public void clear() {
		// null all elements so gc can clean up
		for (int i = 0; i < size; i++) {
			data[i] = null;
		}

		size = 0;
	}

	/**
	 * Add all items into this bag. 
	 * @param added
	 */
	public void addAll(Bag<E> items) {
		for(int i = 0; items.size() > i; i++) {
			add(items.get(i));
		}
	}
	
}
