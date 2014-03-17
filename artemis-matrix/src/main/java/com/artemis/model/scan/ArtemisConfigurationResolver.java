package com.artemis.model.scan;

import org.objectweb.asm.ClassReader;

public final class ArtemisConfigurationResolver {
	private ArtemisConfigurationResolver() {}
	
	public static ArtemisConfigurationData scan(ClassReader source) {
		ArtemisConfigurationData info = new ArtemisConfigurationData();
		source.accept(new ArtemisMetaScanner(info), 0);
		return info;
	}
}
