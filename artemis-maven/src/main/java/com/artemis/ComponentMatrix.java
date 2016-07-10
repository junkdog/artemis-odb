package com.artemis;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.onedaybeard.ecs.model.ComponentDependencyMatrix;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Generates the component dependency report.
 */
@Mojo(name="matrix", defaultPhase=PACKAGE, requiresDependencyResolution=COMPILE)
public class ComponentMatrix extends AbstractMojo {
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Parameter(property="project.build.outputDirectory")
	private File classDirectory;

	@Component
	private BuildContext context;

	@Parameter(property="project.build.directory") 
	private File saveDirectory;
	
	@Parameter(property="project.name")
	private String name;
	
	@Override
	public void execute() throws MojoExecutionException {
		List<URI> files = new ArrayList<URI>();
		files.add(classDirectory.toURI());

		for (Artifact artifact : project.getArtifacts())
			files.add(artifact.getFile().toURI());

		ComponentDependencyMatrix matrix =
			new ComponentDependencyMatrix(name, classDirectory, new File(saveDirectory, "matrix.html"));

		matrix.process();
	}
}
