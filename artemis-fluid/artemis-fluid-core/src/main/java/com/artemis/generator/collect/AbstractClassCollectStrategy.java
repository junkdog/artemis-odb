package com.artemis.generator.collect;

import com.artemis.Component;
import com.artemis.generator.strategy.e.FieldProxyStrategy;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

/**
 * Collector for classes on classpath.
 *
 * @author Daan van Yperen
 */
public abstract class AbstractClassCollectStrategy {

    private final Set<URL> urls;

    /**
     * @param urls locations to search.
     */
    AbstractClassCollectStrategy(Set<URL> urls) {
        this.urls = urls;
    }

    /**
     * Collect all components within a set of URLs
     * @return Set of all components on classloader.
     */
    public abstract Collection<Class<? extends Component>> allComponents();

    /**
     * Collect all components within a set of URLs
     * @return Set of all components on classloader.
     */
    public abstract Collection<Class<? extends FieldProxyStrategy>> allFieldProxyStrategies();
}
