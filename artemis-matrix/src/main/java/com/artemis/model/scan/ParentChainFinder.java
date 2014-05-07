package com.artemis.model.scan;

import static com.artemis.model.scan.TypeConfiguration.type;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ParentChainFinder extends ClassVisitor {

	private Map<Type,Set<Type>> parentChildrenMap;

	public ParentChainFinder(Map<Type,Set<Type>> parentChildrenMap) {
		super(Opcodes.ASM4);
		this.parentChildrenMap = parentChildrenMap;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (superName != null && !"java/lang/Object".equals(superName)) {
			addToMap(type(superName), type(name));
		}
		
		super.visit(version, access, name, signature, superName, interfaces);
	}

	private void addToMap(Type parent, Type child) {
		if (!parentChildrenMap.containsKey(parent))
			parentChildrenMap.put(parent, new HashSet<Type>());
		
		parentChildrenMap.get(parent).add(child);
	}

}
