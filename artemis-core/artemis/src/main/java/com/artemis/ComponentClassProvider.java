package com.artemis;

import com.artemis.annotations.UnstableApi;

import java.util.Collection;

/**
 * Class provider used for Aspects
 *
 * @author Felix Bridault
 */
@UnstableApi
public interface ComponentClassProvider {
    Collection<Class<? extends  Component>> getComponents();
}
