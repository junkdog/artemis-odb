package com.artemis.generator.model.artemis;

import com.artemis.generator.strategy.e.FieldProxyStrategy;
import net.mostlyoriginal.api.utils.Preconditions;
import java.util.Collection;

/**
 * Describes the known artemis universe and generator plugins.
 *
 * @author Daan van Yperen
 */
public class ArtemisModel {
    public Collection<ComponentDescriptor> components;
    public Collection<FieldProxyStrategy> fieldProxyStrategies;

    public ArtemisModel(Collection<ComponentDescriptor> components, Collection<FieldProxyStrategy> fieldProxyStrategies) {
        this.components = Preconditions.checkNotNull(components);
        this.fieldProxyStrategies = Preconditions.checkNotNull(fieldProxyStrategies);
    }
}
