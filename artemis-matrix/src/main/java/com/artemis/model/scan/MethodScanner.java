package com.artemis.model.scan;

import static org.objectweb.asm.Opcodes.ASM4;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class MethodScanner extends MethodVisitor {

	private ArtemisTypeData config;
	private ConfigurationResolver resolver;

	public MethodScanner(ArtemisTypeData config, ConfigurationResolver resolver) {
		super(ASM4);
		this.config = config;
		this.resolver = resolver;
	}
	
	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Type) {
			Type type = (Type)cst;
			if (resolver.components.contains(cst)) {
				config.optional.add(type);
			} else if (resolver.systems.contains(type)) {
				config.systems.add(type);
			} else if (resolver.managers.contains(type)) {
				config.managers.add(type);
			}
		}
		super.visitLdcInsn(cst);
	}
}
