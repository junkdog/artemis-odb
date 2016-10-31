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
 * Collect components using reflections framework.
 *
 * @author Daan van Yperen
 */
public class ReflectionsComponentCollectStrategy extends AbstractComponentCollectStrategy {

    /**
     * Collect all components on a classpath.
     *
     * @param classLoader context.
     * @return Set of all components on classloader.
     */
    public Set<Class<? extends Component>> allComponents(ClassLoader classLoader,Set<URL> urls) {

        // Set the context ClassLoader for this Thread to include all classes.
        // if we don't do this Reflections gets confused and fetches only a subset
        // of components. probably because duplicate entries of Component.class?
        Thread.currentThread().setContextClassLoader(classLoader);

        // reflect over components.
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(urls)
                .setScanners(new SubTypesScanner(true))
                .setExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
        );
        return reflections.getSubTypesOf(Component.class);
    }

    /**
     * Collect all components within a set of URLs
     *
     * @param urls context
     * @return Set of all components on classloader.
     */
    @Override
    public Collection<Class<? extends Component>> allComponents(Set<URL> urls) {
        return allComponents(asClassloader(urls),urls);
    }

    /**
     * Create classloader for URLS
     */
    private ClassLoader asClassloader(Set<URL> urls) {
        return URLClassLoader.newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
    }
}
