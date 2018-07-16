package com.artemis.utils;


/**
 * A non-modifiable intbag.
 */
public interface ImmutableIntBag<E> {


    /**
     * Returns the element at the specified position in Bag.
     *
     * @param index index of the element to return
     * @return the element at the specified position in bag
     */
    int get(int index);

    /**
     * Returns the number of elements in this bag.
     *
     * @return the number of elements in this bag
     */
    int size();

    /**
     * Returns true if this bag contains no elements.
     *
     * @return true if this bag contains no elements
     */
    boolean isEmpty();

    /**
     * Check if bag contains this element.
     *
     * @param id
     * @return true if the bag contains this element
     */
    boolean contains(int id);

}
