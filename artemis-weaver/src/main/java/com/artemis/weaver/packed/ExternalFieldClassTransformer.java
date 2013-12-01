package com.artemis.weaver.packed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadataUtil;
import com.artemis.meta.FieldDescriptor;
import com.artemis.transformer.ClassTransformer;

public class ExternalFieldClassTransformer extends ClassTransformer {

	private final List<ClassMetadata> packedComponents;
	private boolean needsWriteToDisk;

	public ExternalFieldClassTransformer(ClassVisitor ct, List<ClassMetadata> packedComponents) {
		super(null);
		this.packedComponents = packedComponents;
	}

	@Override @SuppressWarnings("unchecked")
	public void transform(ClassNode cn) {
	
		ExternalFieldMethodTransformer methodTransformer = new ExternalFieldMethodTransformer(null, cn.name, packedComponents);
		
		List<MethodNode> methods = cn.methods;
		for (MethodNode method : methods) {
			needsWriteToDisk |= methodTransformer.transform(method);
		}
		
//		List<FieldDescriptor> toPack = ClassMetadataUtil.instanceFields(meta);
		
//		List<String> names = getFieldNames(toPack);
//		FieldToArrayMethodTransformer methodTransformer = new FieldToArrayMethodTransformer(null, meta, getFieldNames(toPack));
//		
//		List<MethodNode> methods = cn.methods;
//		for (MethodNode method : methods) {
//			methodTransformer.transform(method);
//		}
//		
//		for (Iterator<FieldNode> it = cn.fields.iterator(); it.hasNext() ;) {
//			FieldNode next = it.next();
//			if (names.contains(next.name)) {
//				it.remove();
//			}
//		}
//		
		try {
			super.transform(cn);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
//		
//		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		cn.accept(cw);
//		
//		return new ClassReader(cw.toByteArray());
	}

	public boolean isNeedsWriteToDisk() {
		return needsWriteToDisk;
	}
}
