package com.artemis.cli;

import static java.lang.String.format;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.artemis.ClassUtil;
import com.artemis.Weaver;
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
		
		List<ClassMetadata> processed = Weaver.rewriteEntitySystems(ClassUtil.find(classRoot));
		System.out.println(getSummary(processed, start));
		if (verbose && processed.size() > 0)
			System.out.println(getVerbose(processed));
	}
	
	private static CharSequence getSummary(List<ClassMetadata> processed, long start) {
		return String.format("Optimized %d entity systems in %dms.",
			processed.size(), (System.currentTimeMillis() - start));
	}
	
	private static CharSequence getVerbose(List<ClassMetadata> processed) {
		
		Collections.sort(processed, new Comparator<ClassMetadata>() {
			@Override
			public int compare(ClassMetadata o1, ClassMetadata o2) {
				return o1.type.getClassName().compareTo(o2.type.getClassName());
			}
		});
		
		StringBuffer sb = new StringBuffer();
		sb.append("------------------------------------------------------------------------\n");
		for (ClassMetadata meta : processed) {
			sb.append(format("%-61s |%s\n", meta.type.getClassName() + "|", "optimized"));
		}
		sb.append("------------------------------------------------------------------------\n");
		
		String report = sb.toString().replace(' ', '.').replace('|', ' ');
		return report;
	}
}
