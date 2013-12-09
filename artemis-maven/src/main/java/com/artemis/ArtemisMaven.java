package com.artemis;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.artemis.meta.ClassMetadata;

@Mojo(name="artemis", defaultPhase=PROCESS_CLASSES)
public class ArtemisMaven extends AbstractMojo{

	@Parameter(property="project.build.outputDirectory")
	private File outputDirectory;

	@Parameter(property="project.build.sourceDirectory")
	private File sourceDirectory;
	
	@Parameter
	private boolean ideFriendlyPacking;
	
	@Component
	private BuildContext context;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		long start = System.currentTimeMillis();
		if (context != null && !context.hasDelta(sourceDirectory))
			return;
		
		Weaver.retainFieldsWhenPacking(ideFriendlyPacking);
		
		Weaver weaver = new Weaver(outputDirectory);
		List<ClassMetadata> processed = weaver.execute();
		
		Log log = getLog();
		log.info(getSummary(processed, start));
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
