package com.artemis;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

/**
 * The artemis plugin performs bytecode-weaving on annotated components
 * and related classes.
 */
@Mojo(name = "generate", defaultPhase = GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresDependencyCollection = ResolutionScope.COMPILE)
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

    private Log log = getLog();

    @Parameter(property = "project.artifacts", required = true, readonly = true)
    private Set<Artifact> artifacts;

    @Parameter(required = true, property = "project")
    private MavenProject project;

    @Parameter(property = "preferences")
    private MavenFluidGeneratorPreferences preferences = new MavenFluidGeneratorPreferences();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        prepareGeneratedSourcesFolder();
        includeGeneratedSourcesInCompilation();

        new FluidGenerator().generate(
                classpathAsUrls(preferences),
                generatedSourcesDirectory(), createLogAdapter(), preferences);

        System.out.flush();
        System.err.flush();
    }

    /**
     * bridge maven/internal logging.
     */
    private com.artemis.generator.util.Log createLogAdapter() {
        return new com.artemis.generator.util.Log() {
            @Override
            public void info(String msg) {
                log.info(msg);
            }

            @Override
            public void error(String msg) {
                log.error(msg);
            }
        };
    }

    /**
     * Setup generated sources folder if missing.
     */
    private void prepareGeneratedSourcesFolder() {
        if (!generatedSourcesDirectory().exists() && !generatedSourcesDirectory().mkdirs()) {
            log.error("Could not create " + generatedSourcesDirectory());
        }
    }

    /**
     * Must include manually, or maven buids will fail.
     */
    private void includeGeneratedSourcesInCompilation() {
        this.project.addCompileSourceRoot(generatedSourcesDirectory().getPath());
    }

    private Set<URL> classpathAsUrls(MavenFluidGeneratorPreferences preferences) {
        try {
            Set<URL> urls = new HashSet<URL>();
            for (Artifact artifact : artifacts) {
                final URL url = artifact.getFile().toURI().toURL();
                if ( artifact.getFile().exists() ) {
                    if (!preferences.matchesIgnoredClasspath(url.toString())) {
                        urls.add(url);
                        log.info("Including: " + url);
                    }
                } else {
                    log.info("Skipping missing: " + url);
                }
            }
            return urls;
        } catch (MalformedURLException e) {
            throw new RuntimeException("failed to complete classpathAsUrls.", e);
        }
    }

    private File generatedSourcesDirectory() {
        return new File(outputDirectory, "generated-sources/fluid");
    }
}
