package com.artemis.cli;

import java.io.File;
import java.security.ProtectionDomain;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class CliApplication {
	
	@Parameter(
		names = {"-h", "--help"},
		description= "Displays this help message.",
		help = true)
	private boolean help;
	
	public static void main(String[] args) {
		new CliApplication().parse(args);
	}
	
	private void parse(String[] args) {
		MatrixCommand matrix = new MatrixCommand();
		WeaveCommand weave = new WeaveCommand();
		OptimizationCommand optimze = new OptimizationCommand();
		EclipseProcessorCommand eclipse = new EclipseProcessorCommand();
		
		JCommander parser = new JCommander(this);
		parser.addCommand(MatrixCommand.COMMAND, matrix);
		parser.addCommand(WeaveCommand.COMMAND, weave);
		parser.addCommand(OptimizationCommand.COMMAND, optimze);
		parser.addCommand(EclipseProcessorCommand.COMMAND, eclipse);
		
		try {
			parser.setProgramName(getJarName());
			parser.parse(args);
			
			if (help) {
				parser.usage();
				System.exit(0);
			}
			
			String command = parser.getParsedCommand();
			if (MatrixCommand.COMMAND.equals(command)) {
				matrix.execute();
			} else if (WeaveCommand.COMMAND.equals(command)) {
				weave.execute();
			} else if (OptimizationCommand.COMMAND.equals(command)) {
				optimze.execute();
			} else if (EclipseProcessorCommand.COMMAND.equals(command)) {
				eclipse.execute();
			} else {
				parser.usage();
				System.exit(1);
			}
			
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			System.err.println();
			parser.usage();
		}
	}
	
	static String getJarName() {
		ProtectionDomain domain = CliApplication.class.getProtectionDomain();
		String path = domain.getCodeSource().getLocation().getPath();
		
		return new File(path).getName();
	}
}
