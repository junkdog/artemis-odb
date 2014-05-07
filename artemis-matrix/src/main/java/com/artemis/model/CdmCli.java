package com.artemis.model;

import java.io.File;

import com.artemis.model.cli.FileOutputConverter;
import com.artemis.model.cli.FolderConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

public class CdmCli {
	
	@Parameter(
		names = {"-l", "--label"},
		description = "Project name.",
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
		description = "Save to file",
		converter = FileOutputConverter.class,
		required = false)
	private File output = new File("matrix.html");
	
	@Parameter(
		names = {"-h", "--help"},
		description= "Displays this help message.",
		help = true)
	private boolean help;
	
	public static void main(String[] args) {
		new CdmCli().parse(args);
	}
	
	private void parse(String[] args) {
		JCommander parser = new JCommander(this);
		try {
			parser.setProgramName("artemis-odb-matrix");
			parser.parse(args);
			
			if (help) {
				parser.usage();
				System.exit(1);
			}
			
			ComponentDependencyMatrix cdm = 
				new ComponentDependencyMatrix(projectName, classRoot, output);
			cdm.process();
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			System.err.println();
			parser.usage();
		}
	}
}
