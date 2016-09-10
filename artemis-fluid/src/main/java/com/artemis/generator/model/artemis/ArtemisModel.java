package com.artemis.generator.model.artemis;

import java.util.Collection;

/**
 * Describes the known artemis universe.
 *
 * Created by Daan on 11-9-2016.
 */
public class ArtemisModel {
    public Collection<ComponentDescriptor> components;

    public ArtemisModel(Collection<ComponentDescriptor> components) {
        this.components = components;
    }
}
