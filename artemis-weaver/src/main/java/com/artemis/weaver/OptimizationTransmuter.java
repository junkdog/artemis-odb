package com.artemis.weaver;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.optimizer.OptimizingSystemWeaver;
import com.artemis.weaver.optimizer.SystemBytecodeInjector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

public class OptimizationTransmuter extends CallableTransmuter<Void> implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;

	public OptimizationTransmuter(String file, ClassReader cr, ClassMetadata meta) {
		super(file);
		this.cr = cr;
		this.meta = meta;
	}
	
	@Override
	protected Void process(String file) throws IOException {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;
		
		cr = new SystemBytecodeInjector(cr, meta).transform();
		cv = new OptimizingSystemWeaver(cv, meta);
		
		try {
			cr.accept(cv, ClassReader.EXPAND_FRAMES);
			if (file != null) ClassUtil.writeClass(cw, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public ClassWriter getClassWriter() {
		return cw;
	}
	
}
