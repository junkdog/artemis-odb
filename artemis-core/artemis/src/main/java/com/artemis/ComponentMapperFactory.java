package com.artemis;

/**
 * @author Daan van Yperen
 */
public interface ComponentMapperFactory {
    ComponentMapper instance(Class<? extends Component> type, World world);
}
