package com.artemis;

/**
 * Factory for component mappers.
 *
 * @author Daan van Yperen
 */
public interface ComponentMapperFactory {
    /**
     * Create component mapper.
     * @param type Type of component it will map.
     * @param world World the mapper is part of.
     * @return ComponentMapper instance.
     */
    ComponentMapper instance(Class<? extends Component> type, World world);
}
