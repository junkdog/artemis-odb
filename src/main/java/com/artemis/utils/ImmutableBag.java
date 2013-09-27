package com.artemis.utils;


public interface ImmutableBag<E> extends Iterable<E>{

	E get(int index);

	int size();

	boolean isEmpty();
	
	boolean contains(E e);

}
