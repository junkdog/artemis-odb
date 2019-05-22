package com.artemis.utils;

import java.util.Arrays;

import static java.lang.Math.max;


/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 * 
 *
 * @author original Bag by Arni Arent
 */
public class IntBag implements ImmutableIntBag {

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
	 * Removes the first occurrence of the value from this IntBag, if
	 * it is present.
	 *
	 * @param value
	 *			the value to be removed
	 *
	 * @return true, if value was removed
	 */
	public boolean removeValue(int value) throws ArrayIndexOutOfBoundsException {
		int index = indexOf(value);
		if (index > -1)
			removeIndex(index);

		return index > -1;
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
	 * @deprecated Call {@link #removeIndex(int)} instead. {@link #remove(int)} will be removed in 3.0 due to ambiguity.
	 *
	 * @throws ArrayIndexOutOfBoundsException
	 */
	@Deprecated
	public int remove(int index) throws ArrayIndexOutOfBoundsException {
		int e = data[index]; // make copy of element to remove so it can be returned
		data[index] = data[--size]; // overwrite item to remove with last element
		data[size] = 0; // null last element, so gc can do its work
		return e;
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
	public int removeIndex(int index) throws ArrayIndexOutOfBoundsException {
		int e = data[index]; // make copy of element to remove so it can be returned
		data[index] = data[--size]; // overwrite item to remove with last element
		data[size] = 0; // null last element, so gc can do its work
		return e;
	}

	/**
	 * Find index of element.
	 *
	 * @param value
	 *			element to check
	 *
	 * @return index of element, or {@code -1} if there is no such index.
	 */
	public int indexOf(int value) {
		for(int i = 0; size > i; i++) {
			if(value == data[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Check if bag contains this element.
	 *
	 * @param value
	 *			element to check
	 *
	 * @return {@code true} if the bag contains this element
	 */
	public boolean contains(int value) {
		for(int i = 0; size > i; i++) {
			if(value == data[i]) {
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
		if (index >= size) {
			String message = "tried accessing element " + index + "/" + size;
			throw new ArrayIndexOutOfBoundsException(message);
		}

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
	 * @param value
	 *			element to be added to this list
	 */
	public void add(int value) {
		// is size greater than capacity increase capacity
		if (size == data.length)
			grow(2 * data.length);

		data[size++] = value;
	}

	/**
	 * Adds the specified elements to the end of this bag.
	 * <p>
	 * If required, it also increases the capacity of the bag.
	 * </p>
	 *
	 * @param other
	 *			elements to be added to this list
	 */
	public void addAll(IntBag other) {
		for (int i = 0; i < other.size(); i++) {
			add(other.data[i]);
		}
	}

	/**
	 * Set element at specified index in the bag.
	 * 
	 * @param index
	 *			position of element
	 * @param value
	 *			the element
	 */
	public void set(int index, int value) {
		if(index >= data.length) {
			grow(max((2 * data.length), index + 1));
		}

		size = max(size, index + 1);
		data[index] = value;
	}

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
			grow(index + 1);
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
		if (size != intBag.size())
			return false;

		for (int i = 0; size > i; i++) {
			if (data[i] != intBag.data[i])
				return false;
		}

		return true;
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
