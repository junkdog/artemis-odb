package com.artemis.generator.collect;

import com.artemis.Component;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;

/**
 * Collector for classes on classpath.
 *
 * @author Daan van Yperen
 */
public abstract class AbstractComponentCollectStrategy {

    /**
     * Collect all components within a set of URLs
     * @param urls context
     * @return Set of all components on classloader.
     */
    public abstract Collection<Class<? extends Component>> allComponents(Set<URL> urls);

}
