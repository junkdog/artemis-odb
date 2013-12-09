package com.artemis.meta;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClassMetadataUtil {
	
	private ClassMetadata meta;

	public ClassMetadataUtil(ClassMetadata meta) {
		this.meta = meta;
	}
	
	public static List<FieldDescriptor> instanceFields(ClassMetadata meta) {
		List<FieldDescriptor> instanceFields = new ArrayList<FieldDescriptor>();
		for (FieldDescriptor field : meta.fields) {
			if ((field.getAccess() & (ACC_FINAL | ACC_STATIC)) == 0) 
				instanceFields.add(field);
		}
		return instanceFields;
	}
	
	public boolean hasSetter(FieldDescriptor f) {
		String methodDesc = "(" + f.desc + ")";
		for (MethodDescriptor m : meta.methods) {
			if (m.name.equals(f.name) && m.desc.startsWith(methodDesc))
				return true;
		}
		
		return false;
	}
	
	public boolean hasGetter(FieldDescriptor f) {
		String methodDesc = "()" + f.desc;
		for (MethodDescriptor m : meta.methods) {
			if (m.name.equals(f.name) && m.desc.equals(methodDesc))
				return true;
		}
		
		return false;
	}
	
	public static Set<String> instanceFieldTypes(ClassMetadata meta) {
		Set<String> instanceFields = new HashSet<String>();
		for (FieldDescriptor f : instanceFields(meta)) {
			instanceFields.add(f.desc);
		}
		return instanceFields;
	}
	
	public static List<ClassMetadata> packedFieldAccess(Collection<ClassMetadata> components) {
		List<ClassMetadata> packedFieldComponents = new ArrayList<ClassMetadata>();
		for (ClassMetadata c : components) {
			if (PACKED == c.annotation && c.directFieldAccess)
				packedFieldComponents.add(c);
		}
		return packedFieldComponents;
	}
	
	public static String superName(ClassMetadata meta) {
		switch (meta.annotation) {
			case PACKED:
				return "com/artemis/PackedComponent";
			case POOLED:
				return "com/artemis/PooledComponent";
			case NONE:
			default:
				throw new RuntimeException("Missing case : " + meta.annotation);
		}
	}
}
