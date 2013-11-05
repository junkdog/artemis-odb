package com.artemis.meta;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ClassMetadataUtil {
	
	private ClassMetadataUtil() {}
	
	public static List<FieldDescriptor> instanceFields(ClassMetadata meta) {
		List<FieldDescriptor> instanceFields = new ArrayList<FieldDescriptor>();
		for (FieldDescriptor field : meta.fields) {
			if ((field.getAccess() & (ACC_FINAL | ACC_STATIC)) == 0) 
				instanceFields.add(field);
		}
		return instanceFields;
	}
	
	public static Set<String> instanceFieldTypes(ClassMetadata meta) {
		Set<String> instanceFields = new HashSet<String>();
		for (FieldDescriptor f : instanceFields(meta)) {
			instanceFields.add(f.desc);
		}
		return instanceFields;
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
