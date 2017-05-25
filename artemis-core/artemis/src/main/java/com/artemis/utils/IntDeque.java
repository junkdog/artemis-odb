package com.artemis.utils;

import java.util.Arrays;


/**
 * <p>Missing tests and missing methods: (push|peek|pop)(Back|Front).</p>
 */
public class IntDeque {

	private int[] elements;
	private int beginIndex;
	protected int size = 0;

	/**
	 * Constructs an empty Bag with an initial capacity of 64.
	 */
	public IntDeque() {
		this(64);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 *
	 * @param capacity
	 *			the initial capacity of Bag
	 */
	public IntDeque(int capacity) {
		elements = new int[capacity];
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
			if(e == elements[index(i)]) {
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
		return elements[index(index)];
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
		return elements.length;
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
		if (size == elements.length)
			grow((elements.length * 7) / 4 + 1);

		elements[index(size++)] = e;
	}

	private int index(int relativeIndex) {
		return (beginIndex + relativeIndex) % elements.length;
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
		if(index >= elements.length) {
			grow((index * 7) / 4 + 1);
		}
		size = Math.max(size, index + 1);
		elements[index(index)] = e;
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
		int[] newElements = new int[newCapacity];
		for (int i = 0; i < size; i++)
			newElements[i] = get(i);

		elements = newElements;
		beginIndex = 0;
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
		if(index >= elements.length) {
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
		Arrays.fill(elements, 0, size, 0);
		size = 0;
		beginIndex = 0;
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

	public int popLast() {
		assertNotEmpty();

		int index = index(--size);
		int value = elements[index];
		return value;
	}

	public int popFirst() {
		assertNotEmpty();

		int value = elements[beginIndex];
		beginIndex = (beginIndex + 1) % elements.length;
		size--;
		return value;
	}


	private void assertNotEmpty() {
		if (size == 0)
			throw new RuntimeException("Deque is empty.");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntDeque other = (IntDeque) o;
		if (size != other.size) return false;

		for (int i = 0; size > i; i++)
			if (get(i) != other.get(i)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0, s = size; s > i; i++) {
			hash = (127 * hash) + elements[i];
		}

		return hash;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IntDeque(");
		for (int i = 0; size > i; i++) {
			if (i > 0) sb.append(", ");
			sb.append(elements[index(i)]);
		}
		sb.append(')');
		return sb.toString();
	}
}
