package com.artemis.utils;

import com.artemis.annotations.UnstableApi;

import java.util.Arrays;


/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 * 
 *
 * @author original Bag by Arni Arent
 */
@UnstableApi
public class IntBag {

	/** The backing array. */
	private int[] data;
	/** The number of values stored by this bag. */
	protected int size = 0;

	/**
	 * Constructs an empty Bag with an initial capacity of 64.
	 */
	public IntBag() {
		this(64);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 * 
	 * @param capacity
	 *			the initial capacity of Bag
	 */
	public IntBag(int capacity) {
		data = new int[capacity];
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
	public int remove(int index) throws ArrayIndexOutOfBoundsException {
		int e = data[index]; // make copy of element to remove so it can be returned
		data[index] = data[--size]; // overwrite item to remove with last element
		data[size] = 0; // null last element, so gc can do its work
		return e;
	}

	/**
	 * Check if bag contains this element.
	 * 
	 * @param e
	 *			element to check
	 *
	 * @return {@code true} if the bag contains this element
	 */
	public boolean contains(int e) {
		for(int i = 0; size > i; i++) {
			if(e == data[i]) {
				return true;
			}
		}
		return false;
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
	public int get(int index) throws ArrayIndexOutOfBoundsException {
		return data[index];
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
	public void add(int e) {
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
	public void set(int index, int e) {
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
	private void grow(int newCapacity) throws ArrayIndexOutOfBoundsException {
		int[] oldData = data;
		data = new int[newCapacity];
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
		Arrays.fill(data, 0, size, 0);
		size = 0;
	}
	
	/**
	 * Returns this bag's underlying array.
	 * <p>
	 * Use with care.
	 * </p>
	 * 
	 * @return the underlying array
	 *
	 * @see IntBag#size()
	 */
	public int[] getData() {
		return data;
	}

	/**
	 * Set the size.
	 * <p>
	 * This will not resize the bag, nor will it clean up contents beyond the
	 * given size. Use with caution.
	 * </p>
	 *
	 * @param size
	 *			the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntBag intBag = (IntBag) o;
		return size == intBag.size() && Arrays.equals(data, intBag.data);
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0, s = size; s > i; i++) {
			hash = (127 * hash) + data[i];
		}

		return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IntBag(");
		for (int i = 0; size > i; i++) {
			if (i > 0) sb.append(", ");
			sb.append(data[i]);
		}
		sb.append(')');
		return sb.toString();
	}
}
