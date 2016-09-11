package com.artemis;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * The artemis plugin performs bytecode-weaving on annotated components
 * and related classes.
 */
@Mojo(name="generate", defaultPhase=GENERATE_SOURCES)
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

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		if (context != null && !context.hasDelta(sourceDirectory))
			return;

		if ( !generatedSourcesDirectory().mkdirs() )
        {
            log.error("Could not create " + generatedSourcesDirectory());
        }

		new FluidGenerator().generate(new Class[] {Pos.class, Anim.class, Grok.class, Nopping.class},
                generatedSourcesDirectory());
	}

    private File generatedSourcesDirectory() {
        return new File(outputDirectory,"generated-sources");
    }
}
