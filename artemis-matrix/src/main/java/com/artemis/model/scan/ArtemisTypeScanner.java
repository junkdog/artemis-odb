package com.artemis.model.scan;


import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

class ArtemisTypeScanner extends ClassVisitor {
	
	private static final Type COMPONENT_MAPPER = Type.getType("Lcom/artemis/ComponentMapper;");
	
	private final ArtemisTypeData config;
	private final ConfigurationResolver resolver;

	private final Printer printer;
	
	ArtemisTypeScanner(ArtemisTypeData config, ConfigurationResolver configurationResolver) {
		super(Opcodes.ASM4);
		this.config = config;
		this.resolver = configurationResolver;
		
		printer = new ASMifier();
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
//			printing = true;
//			return new TraceMethodVisitor(null, new Textifier());
			
		} else {
//			mv = new MethodScanner(config, resolver);
//			return new ConstructorScanner(config, resolver);
			return null;
		}
		
//		return super.visitMethod(access, name, desc, signature, exceptions);
	}

//	boolean printing = false;
	
//	@Override
//	public void visitEnd() {
//		if (printing) {
//			System.out.println("printing");
//			PrintWriter pw = new PrintWriter(System.out);
//			printer.print(pw);
//			pw.flush();;
//		}
//		
//		super.visitEnd();
//	}
}