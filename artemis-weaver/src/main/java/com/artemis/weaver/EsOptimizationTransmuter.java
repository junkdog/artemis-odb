package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.optimizer.OptimizingEntitySystemWeaver;
import com.artemis.weaver.optimizer.ProcessSystemInjector;

public class EsOptimizationTransmuter extends CallableTransmuter<Void> implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;
	
	public EsOptimizationTransmuter(String file, ClassReader cr, ClassMetadata meta) {
		super(file);
		this.cr = cr;
		this.meta = meta;
	}
	
	@Override
	protected Void process(String file) throws FileNotFoundException, IOException {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;
		
		cr = new ProcessSystemInjector(cr, meta).transform();
		cv = new OptimizingEntitySystemWeaver(cv, meta);
		
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
