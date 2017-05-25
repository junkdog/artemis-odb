package com.artemis.cli.converter;

import java.io.File;

import com.beust.jcommander.IStringConverter;

public class FileOutputConverter implements IStringConverter<File> {

	@Override
	public File convert(String value) {
		File file = new File(value);
		if (file.isDirectory())
			file = new File(file, "matrix.html");
		
		return file;
	}

}
