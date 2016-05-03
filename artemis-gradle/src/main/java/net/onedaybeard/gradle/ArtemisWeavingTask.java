package net.onedaybeard.gradle;

import com.artemis.Weaver;
import com.artemis.WeaverLog;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 * Weaving wrapper for gradle.
 *
 * @author Adrian Papari
 * @author Daan van Yperen
 */
public class ArtemisWeavingTask extends DefaultTask {

	/**
	 * Root folder for class files.
	 */
	@OutputDirectory
	private File classesDir;

	/**
	 * If true, will leave field stubs to keep IDE:s happy after transformations.
	 *
	 * @deprecated no longer has any effect
	 */
	@Input @Deprecated
	private boolean ideFriendlyPacking;

	/**
	 * Enabled weaving of pooled components (more viable on Android than JVM).
	 */
	@Input
	private boolean enablePooledWeaving;

	/**
	 * If false, no weaving will take place (useful for debugging).
	 */
	@Input
	private boolean enableArtemisPlugin;

	@Input
	private boolean optimizeEntitySystems;

	/**
	 * Generate optimized read/write classes for entity link fields, used
	 * by the {@link com.artemis.link.EntityLinkManager}.
	 */
	@Input
	private boolean generateLinkMutators;

	@TaskAction
	public void weave() {
		getLogger().info("Artemis plugin started.");

		if (!enableArtemisPlugin) {
			getLogger().info("Plugin disabled via 'enableArtemisPlugin' set to false.");
			return;
		}

		long start = System.currentTimeMillis();
		//@todo provide gradle alternative.
		//if (context != null && !context.hasDelta(sourceDirectory)) return;

		Logger log = getLogger();
		
//		log.info("");
		log.info("CONFIGURATION");
		log.info(WeaverLog.LINE.replaceAll("\n", ""));
		log.info(WeaverLog.format("enablePooledWeaving", enablePooledWeaving));
		log.info(WeaverLog.format("generateLinkMutators", generateLinkMutators));
		log.info(WeaverLog.format("optimizeEntitySystems", optimizeEntitySystems));
		log.info(WeaverLog.format("outputDirectory",  classesDir));
		log.info(WeaverLog.LINE.replaceAll("\n", ""));
		
		Weaver.enablePooledWeaving(enablePooledWeaving);
		Weaver.generateLinkMutators(generateLinkMutators);
		Weaver.optimizeEntitySystems(optimizeEntitySystems);

		Weaver weaver = new Weaver(classesDir);
		WeaverLog processed = weaver.execute();
		for (String s : processed.getFormattedLog().split("\n")) {
			log.info(s);
		}
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

	public void setGenerateLinkMutators(boolean generateLinkMutators) {
		this.generateLinkMutators = generateLinkMutators;
	}

	public boolean isGenerateLinkMutators() {
		return generateLinkMutators;
	}

	public boolean isOptimizeEntitySystems() {
		return optimizeEntitySystems;
	}

	public void setOptimizeEntitySystems(boolean optimizeEntitySystems) {
		this.optimizeEntitySystems = optimizeEntitySystems;
	}

	public File getClassesDir() {
		return classesDir;
	}

	public void setClassesDir(File classesDir) {
		this.classesDir = classesDir;
	}
}