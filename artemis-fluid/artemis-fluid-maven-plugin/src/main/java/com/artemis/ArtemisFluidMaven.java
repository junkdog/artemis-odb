package com.artemis;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import com.artemis.components.SerializationTag;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * The artemis plugin performs bytecode-weaving on annotated components
 * and related classes.
 */
@Mojo(name = "generate", defaultPhase = GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ArtemisFluidMaven extends AbstractMojo {

    /**
     * Root folder for class files.
     */
    @Parameter(property = "project.build.directory", readonly = true)
    private File outputDirectory;

    /**
     * Root source folder.
     */
    @Parameter(property = "project.build.sourceDirectory", readonly = true)
    private File sourceDirectory;

    @org.apache.maven.plugins.annotations.Component
    private BuildContext context;

    private Log log = getLog();

    @Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
    private List<String> classpathElements;

    @Parameter( required = true, property="project" )
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (context != null && !context.hasDelta(sourceDirectory))
            return;

        prepareGeneratedSourcesFolder();
        includeGeneratedSourcesInCompilation();

        new FluidGenerator().generate(
                collectComponents(),
                generatedSourcesDirectory());
    }

    private List<Class<? extends Component>> collectComponents() {

        final Set<Class<? extends Component>> unfilteredComponents = collectUnfilteredComponents();
        final List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();

        for (Class<? extends Component> component : unfilteredComponents) {
            if (Modifier.isAbstract(component.getModifiers())) {
                log.info(".. Skipping abstract: " + component.toString());
            } else if (component.equals(SerializationTag.class)) {
                log.info(".. Skipping reserved class: " + component.toString());
            } else {
                log.info(".. Including: " + component.toString());
                components.add(component);
            }
        }
        return components;
    }

    /** Setup generated sources folder if missing. */
    private void prepareGeneratedSourcesFolder() {
        if (!generatedSourcesDirectory().exists() && !generatedSourcesDirectory().mkdirs()) {
            log.error("Could not create " + generatedSourcesDirectory());
        }
    }

    /** Must include manually, or maven buids will fail. */
    private void includeGeneratedSourcesInCompilation() {
        this.project.addCompileSourceRoot(  generatedSourcesDirectory().getPath());
    }

    private Set<Class<? extends Component>> collectUnfilteredComponents() {

        // add compile classloader to current classloader.
        // if we don't do this Reflections gets confused and fetches only a subset
        // of components. probably because duplicate entries of Component.class?
        ClassLoader compileClassloader = getCompileClassloader();
        Thread.currentThread().setContextClassLoader(compileClassloader);

        // reflect over components.
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(compileClassloader))
                .setScanners(new SubTypesScanner(false)));

        return reflections.getSubTypesOf(Component.class);
    }

    private ClassLoader getCompileClassloader() {
        try {
            Set<URL> urls = new HashSet<URL>();
            for (String element : classpathElements) {
                URL url = new File(element).toURI().toURL();
                urls.add(url);
                log.info("Adding URL: " + url);
            }

            return URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error generating compile classloader");
        }
    }

    private File generatedSourcesDirectory() {
        return new File(outputDirectory, "generated-sources/fluid");
    }
}
