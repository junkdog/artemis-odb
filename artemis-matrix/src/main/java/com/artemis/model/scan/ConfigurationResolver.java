package com.artemis.model.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.util.ClassFinder;

public final class ConfigurationResolver {
	public final Set<Type> managers;
	public final Set<Type> systems;
	public final Set<Type> components;
	private final TypeConfiguration typeConfiguration;
	
//	public ConfigurationResolver(String basePackage) {
	public ConfigurationResolver(File rootFolder) {
		if (!rootFolder.isDirectory())
			throw new RuntimeException("Expected folder - " + rootFolder);
		
		managers = new HashSet<Type>();
		systems = new HashSet<Type>();
		components = new HashSet<Type>();
		typeConfiguration = new TypeConfiguration();
		
		for (File f : ClassFinder.find(rootFolder)) {
			findArtemisTypes(f);
		}
	}
	
	public ArtemisTypeData scan(ClassReader source) {
		ArtemisTypeData info = new ArtemisTypeData();
		
		ArtemisScanner typeScanner = new ArtemisScanner(info, this);
		source.accept(typeScanner, 0);
		return info;
	}
	
	private void findArtemisTypes(FileInputStream stream) {
		ClassReader cr;
		try {
			cr = new ClassReader(stream);
			ArtemisTypeFinder artemisTypeFinder = new ArtemisTypeFinder(this, typeConfiguration);
			cr.accept(artemisTypeFinder, 0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void findArtemisTypes(File file) {
		try {
			findArtemisTypes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("not found: " + file);
		}
	}
}
