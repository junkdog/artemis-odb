package com.artemis.model.scan;

import static org.objectweb.asm.Opcodes.ASM4;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodScanner extends MethodVisitor {

	private ArtemisTypeData config;
	private ConfigurationResolver resolver;

	public MethodScanner(ArtemisTypeData config, ConfigurationResolver resolver) {
		super(ASM4);
		this.config = config;
		this.resolver = resolver;
	}
}
