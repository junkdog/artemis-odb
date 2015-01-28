package com.artemis.cli;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;

import java.io.File;
import java.util.List;

import com.artemis.Weaver;
import com.artemis.WeaverLog;
import com.artemis.cli.converter.FolderConverter;
import com.artemis.meta.ClassMetadata;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription="Weave component types and inject profiler code into entity systems")
public class WeaveCommand {
	static final String COMMAND = "weave";
	
	@Parameter(
		names = {"-p", "--disable-pooled"},
		description = "Disable weaving of pooled components",
		required = false)
	private boolean disablePooledWeaving = false;

	@Parameter(
		names = {"-P", "--disable-packed"},
		description = "Disable weaving of pooled components",
		required = false)
	private boolean disablePackedWeaving = false;

	@Parameter(
		names = {"-e", "--disable-es-optimization"},
		description = "Disable callsite devirtualization",
		required = false)
	private boolean disableOptimizeEntitySystems = false;

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
		
		Weaver.enablePooledWeaving(!disablePooledWeaving);
		Weaver.enablePackedWeaving(!disablePackedWeaving);
		Weaver.optimizeEntitySystems(!disableOptimizeEntitySystems);
		Weaver weaver = new Weaver(classRoot);
		WeaverLog processed = weaver.execute();
		if (verbose && processed.components.size() > 0) {
			System.out.println(getSummary(processed.components, start));
		} else {
			System.out.println(processed.getFormattedLog());
		}
	}
	
	private static CharSequence getSummary(List<ClassMetadata> processed, long start) {
		int pooled = 0, packed = 0;
		for (ClassMetadata meta : processed) {
			if (PACKED == meta.annotation) packed++;
			else if (POOLED == meta.annotation) pooled++;
		}
		
		return String.format("Processed %d PackedComponents and %d PooledComponents in %dms.",
			packed, pooled, (System.currentTimeMillis() - start));
	}
}
