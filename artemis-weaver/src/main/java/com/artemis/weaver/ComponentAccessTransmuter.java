package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.packed.ExternalFieldClassTransformer;

/**
 * Rewrites access to packed components so that related classes can
 * access packed components with direct field access, syntactically.
 */
public class ComponentAccessTransmuter extends CallableTransmuter implements Opcodes {
	private List<ClassMetadata> packed;
	private ClassReader cr;
	
	public ComponentAccessTransmuter(String file, ClassReader cr, List<ClassMetadata> packedComponents) {
		super(file);
		this.cr = cr;
		this.packed = packedComponents;
	}
	
	@Override
	protected void process(String file) throws FileNotFoundException, IOException {
		compileClass(file);
	}

	private void compileClass(String file) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		
		ExternalFieldClassTransformer transformer = new ExternalFieldClassTransformer(null, packed);
		ClassNode cn = transformer.transform(cr);
		
		if (!transformer.isNeedsWriteToDisk())
			return;
		
		try {
			cn.accept(cw);
			if (file != null)
				ClassUtil.writeClass(cw, file);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
