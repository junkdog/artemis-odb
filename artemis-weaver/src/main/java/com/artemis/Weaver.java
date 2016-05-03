package com.artemis;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.artemis.weaver.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.ClassMetadata.OptimizationType;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.MetaScanner;

public class Weaver {
	public static final String PROFILER_ANNOTATION = "Lcom/artemis/annotations/Profile;";
	public static final String POOLED_ANNOTATION = "Lcom/artemis/annotations/PooledWeaver;";
	public static final String WOVEN_ANNOTATION = "Lcom/artemis/annotations/internal/Transmuted";
	public static final String PRESERVE_VISIBILITY_ANNOTATION = "Lcom/artemis/annotations/PreserveProcessVisiblity;";
	
	private final File targetClasses;
	
	public Weaver(File outputDirectory) {
		this.targetClasses = outputDirectory;
	}
	
	public WeaverLog execute() {
		WeaverLog log = new WeaverLog();
		
		List<File> classes = ClassUtil.find(targetClasses);
		rewriteComponents(classes, log);
		generateLinkMutators(classes, log);
		rewriteProfilers(classes);

		if (ClassMetadata.GlobalConfiguration.optimizeEntitySystems)
			rewriteEntitySystems(classes, log);
		
		sort(log);
		
		return log;
	}
	
	private static void sort(WeaverLog log) {
		Comparator<ClassMetadata> comparator = new Comparator<ClassMetadata>() {
			@Override
			public int compare(ClassMetadata o1, ClassMetadata o2) {
				return o1.type.toString().compareTo(o2.type.toString());
			}
		};
		
		Collections.sort(log.components, comparator);
		Collections.sort(log.componentSystems, comparator);
		Collections.sort(log.systems, comparator);
	}

	public static List<ClassMetadata> rewriteEntitySystems(List<File> classes, WeaverLog log) {
		Timer timer = new Timer();
		List<ClassMetadata> processed = new ArrayList<ClassMetadata>();
		
		ExecutorService threadPool = newThreadPool();
		for (File f : classes) {
			ClassMetadata meta = scan(classReaderFor(f.toString()));
			if (meta.sysetemOptimizable != OptimizationType.NOT_OPTIMIZABLE) {
				processed.add(meta);
				optimizeEntitySystem(threadPool, f.getAbsolutePath());
			}
		}
		awaitTermination(threadPool);
		
		log.timeSystems = timer.duration();
		log.systems = processed;
		
		return processed;
	}

	private static void rewriteProfilers(List<File> classes) {
		ExecutorService threadPool = newThreadPool();
		for (File f : classes)
			processProfilers(threadPool, f.getAbsolutePath());
		
		awaitTermination(threadPool);
	}

	private static void rewriteComponents(List<File> classes, WeaverLog log) {
		Timer timer = new Timer();
		ExecutorService threadPool = newThreadPool();
		
		List<ClassMetadata> processed = new ArrayList<ClassMetadata>();
		for (File f : classes)
			processComponentTypes(threadPool, f.getAbsolutePath(), processed);
		
		awaitTermination(threadPool);
		
		log.components = processed;
		log.timeComponents = timer.duration();
	}

	private static void generateLinkMutators(List<File> classes, WeaverLog log) {
		Timer timer = new Timer();
		ExecutorService threadPool = newThreadPool();

		List<ClassMetadata> processed = new ArrayList<ClassMetadata>();
		for (File f : classes)
			processEntityLinkMutators(threadPool, f.getAbsolutePath(), processed);

		awaitTermination(threadPool);

		log.componentsEntityLinks = processed;
		log.timeComponentsEntityLinks = timer.duration();
	}

	public static void retainFieldsWhenPacking(boolean ideFriendlyPacking) {
		ClassMetadata.GlobalConfiguration.ideFriendlyPacking = ideFriendlyPacking;
	}

	public static void generateLinkMutators(boolean generateLinkMutators) {
		ClassMetadata.GlobalConfiguration.generateLinkMutators = generateLinkMutators;
	}

	public static void enablePooledWeaving(boolean enablePooledWeaving) {
		ClassMetadata.GlobalConfiguration.enabledPooledWeaving = enablePooledWeaving;
	}

	public static void optimizeEntitySystems(boolean enabled) {
		ClassMetadata.GlobalConfiguration.optimizeEntitySystems = enabled;
	}

	private static ExecutorService newThreadPool() {
		return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	private static void processComponentTypes(ExecutorService threadPool, String file, List<ClassMetadata> processed) {
		ClassReader cr = classReaderFor(file);
		ClassMetadata meta = scan(cr);

		if (meta.annotation == WeaverType.NONE)
			return;

		if (meta.annotation == WeaverType.POOLED && !GlobalConfiguration.enabledPooledWeaving) {
			if (!meta.forcePooledWeaving) return;
		}

		threadPool.submit(new ComponentTypeTransmuter(file, cr, meta));
		processed.add(meta);
	}

	private static void processEntityLinkMutators(ExecutorService threadPool, String file, List<ClassMetadata> processed) {
		ClassReader cr = classReaderFor(file);
		ClassMetadata meta = scan(classReaderFor(file));

		if (meta.foundEntityLinks()) {
			threadPool.submit(new EntityLinkGenerator(file, cr, meta));
			processed.add(meta);
		}
	}

	private static void optimizeEntitySystem(ExecutorService threadPool, String file) {
		ClassReader cr = classReaderFor(file);
		ClassMetadata meta = scan(cr);
		
		if (meta.sysetemOptimizable != OptimizationType.NOT_OPTIMIZABLE)
			threadPool.submit(new OptimizationTransmuter(file, cr, meta));
	}

	private static void processProfilers(ExecutorService threadPool, String file) {
		ClassReader cr = classReaderFor(file);
		ClassMetadata meta = scan(cr);
		
		if (meta.profilingEnabled)
			threadPool.submit(new ProfilerTransmuter(file, meta, cr));
	}
	
	static ClassReader classReaderFor(InputStream file) {
		try {
			return new ClassReader(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static ClassReader classReaderFor(String file) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			return classReaderFor(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ClassMetadata scan(ClassReader source) {
		ClassMetadata info = new ClassMetadata();
		info.type = Type.getObjectType(source.getClassName());
		source.accept(new MetaScanner(info), 0);

		return info;
	}

	public static ClassMetadata scan(Class<?> klazz) {
		return scan(toClassReader(klazz));
	}

	public static ClassReader toClassReader(Class<?> klazz) {
		try {
			String resourceName = "/" + klazz.getName().replace('.', '/') + ".class";
			InputStream classStream = klazz.getResourceAsStream(resourceName);
			return new ClassReader(classStream);
		} catch (IOException e) {
			throw new WeaverException("Failed to create reader for " + klazz, e);
		}
	}
	
	private static void awaitTermination(ExecutorService threadPool) {
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static class Timer {
		private long start = System.nanoTime(); 
		
		public int duration() {
			return (int)((System.nanoTime() - start) / 1000000);
		}
	}
}
