package com.artemis.generator.collect;

import com.artemis.Component;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Collect components using reflection.
 *
 * @author Daan van Yperen
 */
public class ReflectionsComponentCollectStrategy {

    /**
     * Collect all components on a classpath.
     * @param classLoader context.
     * @return Set of all components on classloader.
     */
    public Set<Class<? extends Component>> allComponents(ClassLoader classLoader) {

        // Set the context ClassLoader for this Thread.
        // if we don't do this Reflections gets confused and fetches only a subset
        // of components. probably because duplicate entries of Component.class?
        Thread.currentThread().setContextClassLoader(classLoader);

        // reflect over components.
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(classLoader))
                .setScanners(new SubTypesScanner(true))
                .setExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
        );
        return reflections.getSubTypesOf(Component.class);
    }

    /**
     * Collect all components within a set of URLs
     * @param urls context
     * @return Set of all components on classloader.
     */
    public Collection<Class<? extends Component>> allComponents(Set<URL> urls) {
        return allComponents(asClassloader(urls));
    }

    /** Create classloader for URLS */
    private ClassLoader asClassloader(Set<URL> urls) {
        return URLClassLoader.newInstance(
                urls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader());
    }

}
