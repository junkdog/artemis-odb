package com.artemis.weaver;

import static com.artemis.meta.ClassMetadata.WeaverType.PACKED;
import static com.artemis.meta.ClassMetadata.WeaverType.POOLED;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.packed.ExternalFieldClassTransformer;
import com.artemis.weaver.packed.FieldToArrayClassTransformer;
import com.artemis.weaver.packed.PackedComponentWeaver;
import com.artemis.weaver.packed.PackedStubs;
import com.artemis.weaver.pooled.PooledComponentWeaver;

/**
 * Rewrites access to packed components so that related classes can
 * access packed components with direct field access, syntactically.
 */
public class ComponentAccessTransmuter extends CallableTransmuter implements Opcodes {
	private List<ClassMetadata> packed;
	private ClassReader cr;
//	private ClassWriter cw;
	
	public ComponentAccessTransmuter(String file, ClassReader cr, List<ClassMetadata> packedComponents) {
		super(file);
		this.cr = cr;
		this.packed = packedComponents;
	}
	
	@Override
	protected void process(String file) throws FileNotFoundException, IOException {
//		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	
		// scan class for field access on packed components
		
		
		
//		if (POOLED == meta.annotation && !meta.foundReset) {
//			injectMethodStub("reset", "()V");
//		} else if (PACKED == meta.annotation) {
//			cr = new AccessorGenerator(cr, meta).transform();
//			cr = new PackedStubs(cr, meta).transform();
//		}
		
		compileClass(file);
	}

	private void compileClass(String file) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		ClassWriter cw = new ClassWriter(0);
//		ClassVisitor cv = cw;
		
		// FIXME: refactor (encapsulate CN, transform with CW)
		ExternalFieldClassTransformer transformer = new ExternalFieldClassTransformer(null, packed);
		ClassNode cn = new ClassNode(ASM4);
		cr.accept(cn,  ClassReader.EXPAND_FRAMES);
		transformer.transform(cn);
		
		if (!transformer.isNeedsWriteToDisk())
			return;
		
//		cv = new ExternalFieldClassTransformer(null, packed);

//		cn.accept(cw);
//		cr = new ClassReader(cw.toByteArray());
//		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		
		try {
//			cr.accept(cw, ClassReader.EXPAND_FRAMES);
			cn.accept(cw);
			
			String result = ClassUtil.verifyClass(cw);
			System.err.println(file + ":" + result);
//			cr = new ClassReader()
			
//			cw = new ClassWriter(cn, ClassWriter.COMPUTE_FRAMES);
			if (file != null)
				ClassUtil.writeClass(cw, file);
//				ClassUtil.writeClass(cw, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
