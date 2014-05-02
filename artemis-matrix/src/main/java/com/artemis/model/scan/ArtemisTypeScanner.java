package com.artemis.model.scan;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class ArtemisTypeScanner extends ClassVisitor {
	
	private static final Type COMPONENT_MAPPER = Type.getType("Lcom/artemis/ComponentMapper;");
	
	private final ArtemisTypeData config;
	private final ConfigurationResolver resolver;

	ArtemisTypeScanner(ArtemisTypeData config, ConfigurationResolver configurationResolver) {
		super(Opcodes.ASM4);
		this.config = config;
		this.resolver = configurationResolver;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		
		System.out.printf("%s\n", name);
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (!desc.endsWith(";"))
			return super.visitField(access, name, desc, signature, value);
		
		Type type = Type.getType(desc);
		if (resolver.systems.contains(type)) {
			config.systems.add(type);
		} else if (resolver.managers.contains(type)) {
			config.managers.add(type);
		} else if (COMPONENT_MAPPER.equals(type)) {
			String componentDesc = signature.substring(signature.indexOf('<') + 1, signature.indexOf('>'));
			config.optional.add(Type.getType(componentDesc));
		}
		
		return super.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if ("<init>".equals(name)) {
			return new ConstructorScanner(config, resolver);
		} else {
			return new MethodScanner(config, resolver);
		}
	}
}