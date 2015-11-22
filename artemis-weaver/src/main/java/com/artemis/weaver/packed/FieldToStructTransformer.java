
package com.artemis.weaver.packed;


import static com.artemis.meta.ClassMetadataUtil.instanceFields;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.ClassTransformer;

public class FieldToStructTransformer implements ClassTransformer, Opcodes {

	private final ClassMetadata meta;

	public FieldToStructTransformer(ClassMetadata meta) {
		this.meta = meta;
	}
	
	@Override @SuppressWarnings("unchecked")
	public ClassNode transform(ClassReader cr) {
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn,  ClassReader.EXPAND_FRAMES);
		
		List<FieldDescriptor> toPack = instanceFields(meta);
		
		for (FieldDescriptor fd : meta.fields()) {
			
			FieldToStructMethodTransformer methodTransformer = new FieldToStructMethodTransformer(null, meta, fd);
			List<MethodNode> methods = cn.methods;
			for (MethodNode method : methods) {
				methodTransformer.transform(method);
			}
		}
		
		if (!GlobalConfiguration.ideFriendlyPacking) {
			removeFields(cn, getFieldNames(toPack));
		}
		
		return cn;
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
