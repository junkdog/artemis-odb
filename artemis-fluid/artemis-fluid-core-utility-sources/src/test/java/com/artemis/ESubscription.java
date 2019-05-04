package com.artemis;

import com.artemis.annotations.UnstableApi;

import java.util.Iterator;

/**
 * Provides {@code E} access to EntitySubscription with entity subscription.
 *
 * Does not recycle iterator so use this judiciously.
 *
 * @author Daan van Yperen
 */
@UnstableApi
public class ESubscription implements Iterable<E> {
    private final EntitySubscription wrappedSubscription;

    /**
     * Create a new instance of ESubscription.
     * @param wrappedSubscription subscription this ESubscription wraps.
     */
    public ESubscription(EntitySubscription wrappedSubscription) {
        this.wrappedSubscription = wrappedSubscription;
    }

    /**
     * @return Iterator over entities matching the wrapped subscription. Not a flyweight!
     */
    @Override
    public Iterator<E> iterator() {
        return get().iterator();
    }

    /**
     * @return EBag of entities in subscription. Not a flyweight!
     */
    public EBag get() {
        return new EBag(wrappedSubscription.getEntities());
    }

    /**
     * @return number of items in the subscription.
     */
    public int size() {
        return wrappedSubscription.getEntities().size();
    }
}
