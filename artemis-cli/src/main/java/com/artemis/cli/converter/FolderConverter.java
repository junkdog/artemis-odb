package com.artemis.cli.converter;

import java.io.File;

import com.beust.jcommander.IStringConverter;

public class FolderConverter implements IStringConverter<File> {

	@Override
	public File convert(String value) {
		File folder = new File(value);
		if (!folder.isDirectory())
			throw new RuntimeException(value + " is not a folder.");
		return folder;
	}

}
