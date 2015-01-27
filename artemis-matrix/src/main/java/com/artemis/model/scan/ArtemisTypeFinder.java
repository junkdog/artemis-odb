package com.artemis.model.scan;

import static com.artemis.model.scan.TypeConfiguration.type;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ArtemisTypeFinder extends ClassVisitor {

	private TypeConfiguration mainTypes;
	private ConfigurationResolver resolver;

	ArtemisTypeFinder(ConfigurationResolver resolver, TypeConfiguration mainTypes) {
		super(Opcodes.ASM4);
		this.resolver = resolver;
		this.mainTypes = mainTypes;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (superName != null) {
			Type superType = type(superName);
			if (mainTypes.components.contains(superType)) {
				resolver.components.add(type(name));
			} else if (mainTypes.systems.contains(superType)) {
				resolver.systems.add(type(name));
			} else if (mainTypes.managers.contains(superType)) {
				resolver.managers.add(type(name));
			} else if (interfaces.length > 0) {
				for (String iface : interfaces) {
					if (mainTypes.factories.contains(type(iface))) {
						resolver.factories.add(type(name));
					}
				}
			}
		}
		
		super.visit(version, access, name, signature, superName, interfaces);
	}

}
