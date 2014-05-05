package com.artemis;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.apache.maven.plugins.annotations.LifecyclePhase.PACKAGE;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.artemis.model.ComponentDependencyMatrix;

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
