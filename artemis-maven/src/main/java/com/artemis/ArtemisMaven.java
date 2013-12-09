package com.artemis;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.artemis.meta.ClassMetadata;

/**
 * The artemis plugin does bytecode-weaving on annotated components.
 */
@Mojo(name="artemis", defaultPhase=PROCESS_CLASSES)
public class ArtemisMaven extends AbstractMojo {

	/**
	 * Root folder for class files.
	 */
	@Parameter(property="project.build.outputDirectory")
	private File outputDirectory;

	/**
	 * Root source folder.
	 */
	@Parameter(property="project.build.sourceDirectory")
	private File sourceDirectory;
	
	/**
	 * If true, will leave field stubs to keep IDE:s happy after transformations.
	 */
	@Parameter
	private boolean ideFriendlyPacking;
	
	/**
	 * Enabled weaving of pooled components (more viable on Android than JVM).
	 */
	@Parameter(defaultValue="true")
	private boolean enablePooledWeaving;
	
	@Component
	private BuildContext context;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		long start = System.currentTimeMillis();
		if (context != null && !context.hasDelta(sourceDirectory))
			return;
		
		getLog().info(String.format("Configuration:\n\tideFriendlyPacking=%b \n\tenablePooledWeaving=%b ", 
			ideFriendlyPacking, enablePooledWeaving));
		
		Weaver.retainFieldsWhenPacking(ideFriendlyPacking);
		Weaver.enablePooledWeaving(enablePooledWeaving);
		
		Weaver weaver = new Weaver(outputDirectory);
		List<ClassMetadata> processed = weaver.execute();
		
		getLog().info(getSummary(processed, start));
	}
	
	private static CharSequence getSummary(List<ClassMetadata> processed, long start) {
		int pooled = 0, packed = 0;
		for (ClassMetadata meta : processed)
		{
			if (PACKED == meta.annotation) packed++;
			else if (POOLED == meta.annotation) pooled++;
		}
		
		return String.format("Processed %d PackedComponents and %d PooledComponents in %dms.",
			packed, pooled, (System.currentTimeMillis() - start));
	}
}
