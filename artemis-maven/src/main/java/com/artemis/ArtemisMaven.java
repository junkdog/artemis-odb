package com.artemis;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * The artemis plugin performs bytecode-weaving on annotated components
 * and related classes.
 */
@Mojo(name="artemis", defaultPhase=PROCESS_CLASSES)
public class ArtemisMaven extends AbstractMojo {

	/**
	 * Root folder for class files.
	 */
	@Parameter(property = "project.build.outputDirectory", readonly = true)
	private File outputDirectory;

	/**
	 * Root source folder.
	 */
	@Parameter(property = "project.build.sourceDirectory", readonly = true)
	private File sourceDirectory;

	/**
	 * If true, will leave field stubs to keep IDE:s happy after transformations.
	 * @deprecated no longer has any effect
	 */
	@Parameter(property = "ideFriendlyPacking") @Deprecated
	private boolean ideFriendlyPacking;

	/**
	 * Enabled weaving of pooled components (more viable on Android than JVM).
	 */
	@Parameter(defaultValue = "true", property = "enablePooledWeaving")
	private boolean enablePooledWeaving;

	/**
	 * If false, no weaving will take place (useful for debugging).
	 */
	@Parameter(defaultValue = "true", property = "enableArtemisPlugin")
	private boolean enableArtemisPlugin;

	@Parameter(defaultValue = "true", property = "optimizeEntitySystems")
	private boolean optimizeEntitySystems;

	/**
	 * Generate optimized read/write classes for entity link fields, used
	 * by the {@link com.artemis.link.EntityLinkManager}.
	 */
	@Parameter(defaultValue = "true", property = "generateLinkMutators")
	public boolean generateLinkMutators;

	@Component
	private BuildContext context;

	private Log log = getLog();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!enableArtemisPlugin) {
			getLog().info("Plugin disabled via 'enableArtemisPlugin' set to false.");
			return;
		}

		if (context != null && !context.hasDelta(sourceDirectory))
			return;

		log.info("");
		log.info("CONFIGURATION");
		log.info(WeaverLog.LINE.replaceAll("\n", ""));
		log.info(WeaverLog.format("enablePooledWeaving", enablePooledWeaving));
		log.info(WeaverLog.format("generateLinkMutators", generateLinkMutators));
		log.info(WeaverLog.format("optimizeEntitySystems", optimizeEntitySystems));
		log.info(WeaverLog.LINE.replaceAll("\n", ""));

		Weaver.enablePooledWeaving(enablePooledWeaving);
		Weaver.generateLinkMutators(generateLinkMutators);
		Weaver.optimizeEntitySystems(optimizeEntitySystems);

		Weaver weaver = new Weaver(outputDirectory);
		WeaverLog weaverLog = weaver.execute();

		for (String s : weaverLog.getFormattedLog().split("\n")) {
			log.info(s);
		}
	}
}
