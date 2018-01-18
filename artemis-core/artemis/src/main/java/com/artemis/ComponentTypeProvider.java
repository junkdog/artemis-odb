package com.artemis;

import com.artemis.annotations.UnstableApi;

import java.util.Collection;

@UnstableApi
public interface ComponentTypeProvider {
    Collection<Class<? extends  Component>> getComponents();
}
