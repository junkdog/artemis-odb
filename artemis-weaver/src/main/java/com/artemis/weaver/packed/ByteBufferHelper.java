package com.artemis.weaver.packed;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.FieldDescriptor;

class ByteBufferHelper {
	private final ClassMetadata meta;

	ByteBufferHelper(ClassMetadata meta) {
		this.meta = meta;
	}

	AbstractInsnNode invokeGetter(String name) {
		FieldDescriptor fd = find(name);
		return new MethodInsnNode(FieldToStructMethodTransformer.INVOKEVIRTUAL, "java/nio/ByteBuffer", getter(name), "(I)" + castedDesc(fd));
	}
	
	AbstractInsnNode invokePutter(String name) {
		FieldDescriptor fd = find(name);
		
		String desc = "(I" + castedDesc(fd) + ")Ljava/nio/ByteBuffer;";
		return new MethodInsnNode(FieldToStructMethodTransformer.INVOKEVIRTUAL, "java/nio/ByteBuffer", putter(name), desc);
	}

	private static String castedDesc(FieldDescriptor fd) {
		// there's no get/put boolean; booleans are still
		// just normal numbers
		String desc = fd.desc;
		if (desc.equals("Z"))
			desc = "B";
		
		return desc;
	}
	
	private String getter(String name) {
		return "get" + methodSuffix(name);
	}
	
	private String putter(String name) {
		return "put" + methodSuffix(name);
	}
	
	private String methodSuffix(String name) {
		FieldDescriptor fd = find(name);
		switch (fd.desc.charAt(0)) {
			case 'J': // long
				return "Long";
			case 'D': // double
				return "Double";
			case 'I': // int
				return "Int";
			case 'F': // float
				return "Float";
			case 'S': // short
				return "Short";
			case 'C': // char
				return "Char";
			case 'B': // byte
				return "";
			case 'Z': // boolean
				return "";
			default:
				throw new RuntimeException("Unknown primtive type: " + fd.desc);
		}
	}
	
	private FieldDescriptor find(String name) {
		for (FieldDescriptor fd : meta.fields) {
			if (fd.name.equals(name))
				return fd;
		}
		
		throw new RuntimeException("what the hell: " + name);
	}
}