package com.artemis;

import com.artemis.utils.ImmutableBag;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Convenience wrapper, allows iterating over fluid entities.
 *
 * @author Daan van Yperen
 */
public class EBag implements ImmutableBag<E> {

    private final int[] entities;
    private final int size;

    public EBag(int[] entities, int size) {
        this.entities = entities;
        this.size = size;
    }

    public EBag(IntBag bag) {
        this(bag.getData(), bag.size());
    }

    @Override
    public java.util.Iterator<E> iterator() {
        return new EBagIterator(entities, size);
    }

    @Override
    public E get(int index) {
        return E.E(entities[index]);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E value) {
        for (int i = 0; this.size > i; ++i) {
            if (value == E.E(entities[i])) {
                return true;
            }
        }
        return false;
    }

    public static class EBagIterator implements Iterator<E> {
        private final int[] entities;
        private final int size;
        private int cursor;

        private EBagIterator(int[] entities, int size) {
            this.entities = entities;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < size;
        }

        @Override
        public E next() {
            if (this.cursor == size) {
                throw new NoSuchElementException("Iterated past last element");
            } else {
                return E.E(entities[this.cursor++]);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}