
package com.artemis.weaver.packed;


import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.ClassTransformer;

public class FieldToArrayClassTransformer extends ClassTransformer implements Opcodes {

	private final ClassMetadata meta;

	public FieldToArrayClassTransformer(ClassTransformer ct, ClassMetadata meta) {
		super(ct);
		this.meta = meta;
	}
	
	@Override @SuppressWarnings("unchecked")
	public void transform(ClassNode cn) {
		
		List<FieldDescriptor> toPack = instanceFields(meta);
		FieldToArrayMethodTransformer methodTransformer = new FieldToArrayMethodTransformer(null, meta, getFieldNames(toPack));
		
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			methodTransformer.transform(method);
		}
		
		if (!GlobalConfiguration.ideFriendlyPacking) {
			removeFields(cn, getFieldNames(toPack));
		}
		
		try {
			super.transform(cn);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void removeFields(ClassNode cn, List<String> names) {
		for (Iterator<FieldNode> it = cn.fields.iterator(); it.hasNext() ;) {
			FieldNode next = it.next();
			if (names.contains(next.name)) {
				it.remove();
			}
		}
	}

	private static List<String> getFieldNames(List<FieldDescriptor> toPack) {
		List<String> names = new ArrayList<String>();
		for (FieldDescriptor fd : toPack) {
			names.add(fd.name);
		}
		return names;
	}
}
