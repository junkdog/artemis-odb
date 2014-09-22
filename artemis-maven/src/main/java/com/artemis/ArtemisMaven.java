package com.artemis;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.artemis.meta.ClassMetadata;

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
	 */
	@Parameter(property = "ideFriendlyPacking")
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

	@Component
	private BuildContext context;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!enableArtemisPlugin) {
			getLog().info("Plugin disabled via 'enableArtemisPlugin' set to false.");
			return;
		}

		long start = System.currentTimeMillis();
		if (context != null && !context.hasDelta(sourceDirectory))
			return;

		Log log = getLog();
		log.info("Configuration:");
		log.info("\tideFriendlyPacking .............. " + ideFriendlyPacking);
		log.info("\tenablePooledWeaving ............. " + enablePooledWeaving);
		log.info("\toptimizeEntitySystems ........... " + optimizeEntitySystems);

		Weaver.retainFieldsWhenPacking(ideFriendlyPacking);
		Weaver.enablePooledWeaving(enablePooledWeaving);
		Weaver.optimizeEntitySystems(optimizeEntitySystems);

		Weaver weaver = new Weaver(outputDirectory);
		List<ClassMetadata> processed = weaver.execute();

		log.info(getSummary(processed, start));

		for (ClassMetadata meta : processed) {
			try {
				meta.weaverTask.get();
			} catch (InterruptedException e) {
				throw new MojoExecutionException(e.getMessage(), e);
			} catch (ExecutionException e) {
				throw new MojoExecutionException(e.getCause().getMessage(), e.getCause());
			}
		}
	}

	private static CharSequence getSummary(List<ClassMetadata> processed, long start) {
		int pooled = 0, packed = 0;
		for (ClassMetadata meta : processed) {
			if (PACKED == meta.annotation) packed++;
			else if (POOLED == meta.annotation) pooled++;
		}

		return String.format("Processed %d PackedComponents and %d PooledComponents in %dms.",
				packed, pooled, (System.currentTimeMillis() - start));
	}

}
