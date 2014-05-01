package com.artemis.model.scan;

import static org.objectweb.asm.Opcodes.ASM4;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class ConstructorScanner extends MethodVisitor {

	private final ArtemisTypeData config;
	private final ConfigurationResolver resolver;
	
	private final Set<Type> queuedComponents;
	
	private static final String[] ASPECT_REQUIRE = {"getAspectForAll", "getAspectFor", "all"};
	private static final String[] ASPECT_REQUIRE_ONE = {"one", "getAspectForOne"};
	private static final String[] ASPECT_EXCLUDE = {"exclude"};
	static {
		Arrays.sort(ASPECT_REQUIRE);
		Arrays.sort(ASPECT_REQUIRE_ONE);
		Arrays.sort(ASPECT_EXCLUDE);
	}
	
	public ConstructorScanner(ArtemisTypeData config, ConfigurationResolver resolver) {
		super(ASM4);
		this.config = config;
		this.resolver = resolver;
		queuedComponents = new HashSet<Type>();
		
		System.out.println("new " + this);
	}
	
	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Type && resolver.components.contains(cst)) {
			queuedComponents.add((Type)cst);
		}
		super.visitLdcInsn(cst);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if (Arrays.binarySearch(ASPECT_REQUIRE, name) >= 0) {
			config.requires.addAll(queuedComponents);
			queuedComponents.clear();
		} else if (Arrays.binarySearch(ASPECT_REQUIRE_ONE, name) >= 0) {
			config.requiresOne.addAll(queuedComponents);
			queuedComponents.clear();
		} else if (Arrays.binarySearch(ASPECT_EXCLUDE, name) >= 0) {
			config.exclude.addAll(queuedComponents);
			queuedComponents.clear();
		}
		
		super.visitMethodInsn(opcode, owner, name, desc);
	}
}
