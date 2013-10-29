package com.artemis.meta;

import org.objectweb.asm.ClassReader;

public class AnnotationScanner {

	private AnnotationScanner() {}
	
	public static ClassMetadata scan(ClassReader source) {
		ClassMetadata info = new ClassMetadata();
		source.accept(new MetaScanner(info), 0);
		return info;
	}
}
