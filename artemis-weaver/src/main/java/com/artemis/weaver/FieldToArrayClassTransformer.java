
package com.artemis.weaver;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.ClassTransformer;

public class FieldToArrayClassTransformer extends ClassTransformer implements Opcodes {

	private final ClassMetadata meta;
	private FieldToArrayMethodTransformer methodTransformer;

	public FieldToArrayClassTransformer(ClassTransformer ct, ClassMetadata meta) {
		super(ct);
		this.meta = meta;
		this.methodTransformer = new FieldToArrayMethodTransformer(null, meta);
	}
	
	@Override
	public void transform(ClassNode cn) {
		
		List<FieldDescriptor> toPack = ClassMetadataUtil.instanceFields(meta);
		Set<String> names = getFieldNames(toPack);
		
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			methodTransformer.transform(method);
		}
		
		List<FieldNode> fields = cn.fields;
		for (Iterator<FieldNode> it = fields.iterator(); it.hasNext() ;) {
			FieldNode next = it.next();
			if (names.contains(next.name)) {
				it.remove();
			}
		}
		
		super.transform(cn);
	}

	private static Set<String> getFieldNames(List<FieldDescriptor> toPack) {
		Set<String> names = new HashSet<String>();
		for (FieldDescriptor fd : toPack) {
			names.add(fd.name);
		}
		return names;
	}
}
