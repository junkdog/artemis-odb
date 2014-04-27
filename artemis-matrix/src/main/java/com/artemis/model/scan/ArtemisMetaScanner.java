package com.artemis.model.scan;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

// TODO: if manager or system, scan method bodies
class ArtemisMetaScanner extends ClassVisitor {
	
	private final ArtemisConfigurationData config;
	private final ArtemisConfigurationResolver resolver;

	ArtemisMetaScanner(ArtemisConfigurationData config, ArtemisConfigurationResolver configurationResolver) {
		super(Opcodes.ASM4);
		this.config = config;
		this.resolver = configurationResolver;
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		Type type = Type.getType(desc);
		if (config.systems.contains(type)) {
			
		} else if (config.managers.contains(type)) {
			
		}
		
		// TODO: check component mappers
		
		return super.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ("<init>".equals(name))
			return new ArtemisConstructorReader(config, resolver);
		else
			return new ArtemisMethodReader(config, resolver);
	}
}