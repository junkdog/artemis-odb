package net.onedaybeard.gradle;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;

import com.artemis.Weaver;
import com.artemis.meta.ClassMetadata;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.mvn3.org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Daan van Yperen
 */
public class ArtemisWeavingTask extends DefaultTask {

	/** Root folder for class files. */
	@OutputDirectory
	private File outputDirectory;

	/** Root source folder. */
	@InputDirectory
	private File sourceDirectory;

	/** If true, will leave field stubs to keep IDE:s happy after transformations. */
	@Input
	private boolean ideFriendlyPacking;

	/** Enabled weaving of pooled components (more viable on Android than JVM). */
	@Input
	private boolean enablePooledWeaving;

	/** If false, no weaving will take place (useful for debugging). */
	@Input
	private boolean enableArtemisPlugin;

	@Input
	private boolean optimizeEntitySystems;

	@TaskAction
	public void weavingTask() {
		getLogger().info("Artemis plugin started.");

		if (!enableArtemisPlugin) {
			getLogger().info("Plugin disabled via 'enableArtemisPlugin' set to false.");
			return;
		}

		long start = System.currentTimeMillis();
		//@todo provide gradle alternative.
		//if (context != null && !context.hasDelta(sourceDirectory)) return;

		Logger log = getLogger();
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
				throw new RuntimeException(e.getMessage(), e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e.getCause().getMessage(), e.getCause());
			}
		}
	}

	private static String getSummary(List<ClassMetadata> processed, long start) {
		int pooled = 0, packed = 0;
		for (ClassMetadata meta : processed) {
			if (PACKED == meta.annotation) packed++;
			else if (POOLED == meta.annotation) pooled++;
		}

		return String.format("Processed %d PackedComponents and %d PooledComponents in %dms.",
				packed, pooled, (System.currentTimeMillis() - start));
	}

	public boolean isEnableArtemisPlugin() {
		return enableArtemisPlugin;
	}

	public void setEnableArtemisPlugin(boolean enableArtemisPlugin) {
		this.enableArtemisPlugin = enableArtemisPlugin;
	}

	public boolean isIdeFriendlyPacking() {
		return ideFriendlyPacking;
	}

	public void setIdeFriendlyPacking(boolean ideFriendlyPacking) {
		this.ideFriendlyPacking = ideFriendlyPacking;
	}

	public boolean isEnablePooledWeaving() {
		return enablePooledWeaving;
	}

	public void setEnablePooledWeaving(boolean enablePooledWeaving) {
		this.enablePooledWeaving = enablePooledWeaving;
	}

	public boolean isOptimizeEntitySystems() {
		return optimizeEntitySystems;
	}

	public void setOptimizeEntitySystems(boolean optimizeEntitySystems) {
		this.optimizeEntitySystems = optimizeEntitySystems;
	}
}