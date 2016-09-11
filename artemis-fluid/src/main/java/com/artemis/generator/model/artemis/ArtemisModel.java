package com.artemis.generator.model.artemis;

import java.util.Collection;

/**
 * Describes the known artemis universe.
 *
 * @author Daan van Yperen
 */
public class ArtemisModel {
    public Collection<ComponentDescriptor> components;

    public ArtemisModel(Collection<ComponentDescriptor> components) {
        this.components = components;
    }
}
