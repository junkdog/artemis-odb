package com.artemis.cli;

import java.io.File;

import com.artemis.cli.converter.FileOutputConverter;
import com.artemis.cli.converter.FolderConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.onedaybeard.ecs.model.ComponentDependencyMatrix;

@Parameters(
		commandDescription="Generate the Component Dependency Matrix from existing classes")
public class MatrixCommand {
	static final String COMMAND = "matrix";
	
	@Parameter(
		names = {"-l", "--label"},
		description = "Project name, used as page title",
		required = false)
	private String projectName = "Unknown artemis project";
	
	@Parameter(
		names = {"-c", "--class-folder"},
		description = "Root class folder",
		converter = FolderConverter.class,
		required = true)
	private File classRoot;
	
	@Parameter(
		names = {"-o", "--output"},
		description = "Save to file, destination may be given as a folder path",
		converter = FileOutputConverter.class,
		required = false)
	private File output = new File("matrix.html");
	
	void execute() {
		ComponentDependencyMatrix cdm =
			new ComponentDependencyMatrix(projectName, classRoot, output);
		cdm.process();
	}
}
