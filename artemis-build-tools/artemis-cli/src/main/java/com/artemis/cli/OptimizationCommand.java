package com.artemis.cli;

import java.io.File;
import java.util.List;

import com.artemis.ClassUtil;
import com.artemis.Weaver;
import com.artemis.WeaverLog;
import com.artemis.cli.converter.FolderConverter;
import com.artemis.meta.ClassMetadata;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription="Optimize systems extending EntityProcessingSystem")
public class OptimizationCommand {
	static final String COMMAND = "optimize";
	
	@Parameter(
		names = {"-c", "--class-folder"},
		description = "Root class folder",
		converter = FolderConverter.class,
		required = true)
	private File classRoot;
	
	@Parameter(
		names = {"-v", "--verbose"},
		description = "Print summary of all transformed components",
		required = false)
	private boolean verbose = false;
	
	void execute() {
		long start = System.currentTimeMillis();
		
		WeaverLog result = new WeaverLog();
		List<ClassMetadata> processed = 
				Weaver.rewriteEntitySystems(ClassUtil.find(classRoot), result);
		
		
	
		if (verbose && processed.size() > 0) {
			System.out.println(result.getFormattedLog());
		} else {
			System.out.println(getSummary(processed, start));
		}
	}
	
	private static CharSequence getSummary(List<ClassMetadata> processed, long start) {
		return String.format("Optimized %d entity systems in %dms.",
			processed.size(), (System.currentTimeMillis() - start));
	}
}
