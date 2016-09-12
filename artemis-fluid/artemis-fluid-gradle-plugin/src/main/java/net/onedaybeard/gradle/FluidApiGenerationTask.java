package net.onedaybeard.gradle;

import com.artemis.FluidGenerator;
import com.artemis.Weaver;
import com.artemis.WeaverLog;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Weaving wrapper for gradle.
 *
 * @author Adrian Papari
 * @author Daan van Yperen
 */
public class FluidApiGenerationTask extends DefaultTask {


	@Input
	private File generatedSourcesDirectory;

	@Input
	private FileCollection classpath;

	private Logger log = getLogger();

	@TaskAction
	public void fluid() {
		log.info("Artemis Fluid api plugin started.");


		prepareGeneratedSourcesFolder();
		includeGeneratedSourcesInCompilation();

		new FluidGenerator().generate(
				classpathAsUrls(),
				generatedSourcesDirectory, createLogAdapter());
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
		if (!generatedSourcesDirectory.exists() && !generatedSourcesDirectory.mkdirs()) {
			log.error("Could not create " + generatedSourcesDirectory);
		}
	}

	/**
	 * Must include manually, or maven buids will fail.
	 */
	private void includeGeneratedSourcesInCompilation() {
//		getProject().addCompileSourceRoot(generatedSourcesDirectory().getPath());
	}

	private Set<URL> classpathAsUrls() {
		try {
			Set<URL> urls = new HashSet<URL>();
			for (File element : classpath) {
				URL url;
				url = element.toURI().toURL();
				urls.add(url);
				log.info("Including: " + url);
			}
			return urls;
		} catch (MalformedURLException e) {
			throw new RuntimeException("failed to complete classpathAsUrls.", e);
		}
	}

	public File getGeneratedSourcesDirectory() {
		return generatedSourcesDirectory;
	}

	public void setGeneratedSourcesDirectory(File generatedSourcesDirectory) {
		this.generatedSourcesDirectory = generatedSourcesDirectory;
	}

	public FileCollection getClasspath() {
		return classpath;
	}

	public void setClasspath(FileCollection classpath) {
		this.classpath = classpath;
	}
}