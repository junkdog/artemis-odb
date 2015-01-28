package com.artemis;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;

import net.onedaybeard.ecs.model.ComponentDependencyMatrix;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates the component dependency report.
 */
@Mojo(name="matrix", defaultPhase=PACKAGE, requiresDependencyResolution=COMPILE_PLUS_RUNTIME)
public class ComponentMatrix extends AbstractMojo
{
	@Parameter(property="project.build.outputDirectory")
	private File classDirectory;

	@Parameter(property="project.build.sourceDirectory")
	private File sourceDirectory;
	
	@Component
	private BuildContext context;

	@Parameter(property="project.build.directory") 
	private File saveDirectory;
	
	@Parameter(property="project.name")
	private String name;
	
	@Override
	public void execute() throws MojoExecutionException
	{
		ComponentDependencyMatrix matrix =
			new ComponentDependencyMatrix(name, classDirectory, new File(saveDirectory, "matrix.html"));
		matrix.process();
	}
}
