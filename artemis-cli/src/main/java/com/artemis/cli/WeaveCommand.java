package com.artemis.cli;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.artemis.Weaver;
import com.artemis.cli.converter.FolderConverter;
import com.artemis.meta.ClassMetadata;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
		commandDescription="Weave packed and pooled components")
public class WeaveCommand {
	static final String COMMAND = "weave";
	
	@Parameter(
		names = {"-p", "--disable-pooled"},
		description = "Disable weaving of pooled components",
		required = false)
	private boolean disablePooledWeaving = false;
	
	@Parameter(
		names = {"-c", "--class-folder"},
		description = "Root class folder",
		converter = FolderConverter.class,
		required = true)
	private File classRoot;
	
	@Parameter(
		names = {"-v", "--verbose"},
		description = "Print summary of all transformed classes",
		required = false)
	private boolean verbose = false;
	
	void execute() {
		long start = System.currentTimeMillis();
		
		Weaver.enablePooledWeaving(!disablePooledWeaving);
		Weaver weaver = new Weaver(classRoot);
		List<ClassMetadata> processed = weaver.execute();
		for (ClassMetadata meta : processed) {
			try {
				meta.weaverTask.get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e.getCause().getMessage(), e.getCause());
			}
		}
		System.out.println(getSummary(processed, start));
		if (verbose && processed.size() > 0)
			System.out.println(getVerbose(processed));
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
			sb.append(String.format("%-64s |%s\n",
					meta.type.getClassName() + "|", meta.annotation));
		}
		sb.append("------------------------------------------------------------------------\n");
		
		String report = sb.toString().replace(' ', '.').replace('|', ' ');
		return report;
	}
}
