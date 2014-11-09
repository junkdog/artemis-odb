package com.artemis.cli.converter;

import java.io.File;

import com.beust.jcommander.IStringConverter;

public class EclipseProjectFolder implements IStringConverter<File> {

	@Override
	public File convert(String value) {
		File folder = new File(value);
		
		if (!folder.isDirectory())
			throw new RuntimeException(value + " is not a folder.");
		
		if (!new File(folder, ".project").exists())
			throw new RuntimeException("Not an eclipse project: " + folder);
			
		
		return folder;
	}

}
