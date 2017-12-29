package com.artemis.generator.collect;

import com.artemis.Component;
import com.artemis.generator.strategy.e.FieldProxyStrategy;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Collect classes using reflections framework.
 *
 * @author Daan van Yperen
 */
public class ReflectionsClassCollectStrategy extends AbstractClassCollectStrategy {

    private final Reflections reflections;

    public ReflectionsClassCollectStrategy(Set<URL> urls) {
        super(urls);

        ClassLoader classLoader = asClassloader(urls);

        // Set the context ClassLoader for this Thread to include all classes.
        // if we don't do this Reflections gets confused and fetches only a subset
        // of components. probably because duplicate entries of Component.class?
        Thread.currentThread().setContextClassLoader(classLoader);

        reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(urls)
                .setScanners(new SubTypesScanner(true))
                .setExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
        );
    }

    /**
     * Get all components on classpath.
     *
     * @return Set of all subtypes of {@link Component}.
     */
    @Override
    public Set<Class<? extends Component>> allComponents() {
        // reflect over components.
        return reflections.getSubTypesOf(Component.class);
    }


    /**
     * Get all field proxy strategies on classpath.
     *
     * @return Set of all subtypes of {@link FieldProxyStrategy}
     */
    @Override
    public Set<Class<? extends FieldProxyStrategy>> allFieldProxyStrategies() {
        // reflect over components.
        return reflections.getSubTypesOf(FieldProxyStrategy.class);
    }

    /**
     * Create classloader for URLS
     */
    private static ClassLoader asClassloader(Set<URL> urls) {
        return URLClassLoader.newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
    }
}
