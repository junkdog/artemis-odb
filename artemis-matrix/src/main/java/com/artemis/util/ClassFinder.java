package com.artemis.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ClassFinder
{
	private ClassFinder() {}
	
	public static List<File> find(String root)
	{
		return find(new File(root));
	}
	
	public static List<File> find(File root)
	{
		if (!root.isDirectory())
			throw new IllegalAccessError(root + " must be a folder.");
		
		List<File> klazzes = new ArrayList<File>();
		addFiles(klazzes, root);
			
		return klazzes;
	}
	
	private static void addFiles(List<File> files, File folder)
	{
		for (File f : folder.listFiles())
		{
			if (f.isFile() && f.getName().endsWith(".class"))
				files.add(f);
			else if (f.isDirectory())
				addFiles(files, f);
		}
	}
}
