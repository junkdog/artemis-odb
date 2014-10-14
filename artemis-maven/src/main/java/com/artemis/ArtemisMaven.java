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
		log.info(WeaverLog.format("ideFriendlyPacking", ideFriendlyPacking));
		log.info(WeaverLog.format("enablePooledWeaving", enablePooledWeaving));
		log.info(WeaverLog.format("optimizeEntitySystems", optimizeEntitySystems));
		log.info(WeaverLog.LINE.replaceAll("\n", ""));

		Weaver.retainFieldsWhenPacking(ideFriendlyPacking);
		Weaver.enablePooledWeaving(enablePooledWeaving);
		Weaver.optimizeEntitySystems(optimizeEntitySystems);

		Weaver weaver = new Weaver(outputDirectory);
		WeaverLog weaverLog = weaver.execute();

//		log.info("");
//		log.info(format("WOVEN COMPONENTS", weaverLog.timeComponents + "ms", ' '));
//		log.info(LINE);
//		for (String detail : getComponentSummary(weaverLog.components).split("\n"))
//			log.info(detail);
//		log.info(LINE);
//		
//		if (weaverLog.timeComponentSystems > 0) {
//			log.info("");
//			log.info(format("COMPONENT ACCESS REWRITTEN", weaverLog.timeComponentSystems + "ms", ' '));
//			log.info(LINE);
//			for (String detail : getRewrittenAccessSummary(weaverLog.componentSystems).split("\n"))
//				log.info(detail);
//			log.info(LINE);
//		}
//		
//		if (weaverLog.timeSystems > 0) {
//			log.info("");
//			log.info(format("OPTIMIZED ENTITY SYSTEMS", weaverLog.timeSystems + "ms", ' '));
//			log.info(LINE);
//			for (String detail : getSystemSummary(weaverLog.systems).split("\n"))
//				log.info(detail);
//			log.info(LINE);
//		}
		for (String s : weaverLog.getFormattedLog().split("\n")) {
			log.info(s);
		}
	}
	
//	private static String format(String key, Object value, char delim) {
//		int length = key.length() + value.toString().length() + 2; // margin
//		length = Math.max(length, 3);
//		
//		char[] padding = new char[RELATIVE_WIDTH - length];
//		Arrays.fill(padding, delim);
//		
//		return new StringBuilder(RELATIVE_WIDTH)
//			.append(key)
//			.append(" ").append(String.valueOf(padding)).append(" ")
//			.append(value)
//			.toString();
//	}
	
//	private static String format(String key, Object value) {
//		return format(key, value, '.');
//	}
//	
//	private static String horizontalLine() {
//		char[] raw = new char[RELATIVE_WIDTH];
//		Arrays.fill(raw, '-');
//		return String.valueOf(raw);
//	}
//
//	private static String getComponentSummary(List<ClassMetadata> processed) {
//		StringBuilder sb = new StringBuilder();
//		
//		for (ClassMetadata meta : processed) {
//			if (meta.annotation == WeaverType.NONE)
//				continue;
//			
//			String klazz = shortenClass(meta.type);
//			sb.append(format(klazz, meta.annotation.name())).append("\n");
//		}
//		
//		return sb.toString();
//	}
//	
//	private static String shortenClass(Type type) {
//		return shortenClass(type.getClassName());
//	}
//	
//	private static String shortenClass(String className) {
//		StringBuilder sb = new StringBuilder();
//		
//		String[] split = className.split("\\.");
//		for (int i = 0; (split.length - 1) > i; i++) {
//			sb.append(split[i].charAt(0)).append('.');
//		}
//		sb.append(split[split.length - 1]);
//		return sb.toString();
//	}
//
//	private static String getSystemSummary(List<ClassMetadata> processed) {
//		StringBuilder sb= new StringBuilder();
//		
//		for (ClassMetadata meta : processed) {
//			String klazz = shortenClass(meta.type);
//			sb.append(format(klazz, meta.sysetemOptimizable.name())).append("\n");
//		}
//		
//		return sb.toString();
//	}
//
//	private static String getRewrittenAccessSummary(List<ClassMetadata> processed) {
//		StringBuilder sb= new StringBuilder();
//		
//		for (ClassMetadata meta : processed) {
//			String klazz = shortenClass(meta.type);
//			sb.append(format(klazz, "SUCCESS")).append("\n");
//		}
//		
//		return sb.toString();
//	}
}
