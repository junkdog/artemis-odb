package com.artemis.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

/**
 * Tests for similar behavior to Iterator.
 *
 * @author Daan van Yperen
 */
public class IntBagIteratorTest {

    @Test
    public void hasNext_on_an_empty_collection__returns_false() {
        IntBagIterator intBagIterator = new IntBagIterator(new IntBag(99));
        Assert.assertFalse(intBagIterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void next___on_an_empty_collection__throws_exception() {
        IntBagIterator intBagIterator = new IntBagIterator(new IntBag(99));
        intBagIterator.next();
    }

    @Test
    public void hasNext_on_a_collection_with_one_item__returns_true_several_times() {
        IntBag bag = new IntBag(1);
        bag.add(8008);
        IntBagIterator intBagIterator = new IntBagIterator(bag);
        Assert.assertTrue(intBagIterator.hasNext());
        Assert.assertTrue(intBagIterator.hasNext());
        Assert.assertTrue(intBagIterator.hasNext());
    }

    @Test
    public void hasNext_next_on_a_collection_with_one_item__hasNext_returns_true_next_returns_the_item_hasNext_returns_false_twice() {
        IntBag bag = new IntBag(1);
        bag.add(8008);
        IntBagIterator intBagIterator = new IntBagIterator(bag);
        Assert.assertTrue(intBagIterator.hasNext());
        Assert.assertNotNull(intBagIterator.next());
        Assert.assertFalse(intBagIterator.hasNext());
        Assert.assertFalse(intBagIterator.hasNext());
    }

    @Test
    public void remove_on_that_collection__check_size_is_0_after() {
        IntBag bag = new IntBag(20);
        bag.add(8008);
        IntBagIterator intBagIterator = new IntBagIterator(bag);
        intBagIterator.next();
        intBagIterator.remove();
        Assert.assertEquals(0, bag.size());
    }

    @Test(expected = IllegalStateException.class)
    public void remove_again__exception() {
        IntBag bag = new IntBag(20);
        bag.add(8008);
        IntBagIterator intBagIterator = new IntBagIterator(bag);
        intBagIterator.next();
        intBagIterator.remove();
        intBagIterator.remove();
    }

    @Test
    public void with_a_collection_with_several_items_make_sure_the_iterator_goes_through_each_item_in_the_correct_order__if_there_is_one_() {
        IntBag bag = new IntBag(20);
        bag.add(8008);
        bag.add(8009);
        bag.add(8010);
        IntBagIterator intBagIterator = new IntBagIterator(bag);
        Assert.assertEquals(8008, intBagIterator.next());
        Assert.assertEquals(8009, intBagIterator.next());
        Assert.assertEquals(8010, intBagIterator.next());
    }

    @Test
    public void remove_all_elements_from_the_collection__collection_is_now_empty() {
        IntBag bag = new IntBag(20);
        bag.add(8008);
        bag.add(8009);
        IntBagIterator intBagIterator = new IntBagIterator(bag);
        intBagIterator.next();
        intBagIterator.remove();
        intBagIterator.next();
        intBagIterator.remove();
        Assert.assertEquals(0, bag.size());
    }
}