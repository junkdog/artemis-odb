package com.artemis.weaver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.ClassUtil;
import com.artemis.meta.ClassMetadata;
import com.artemis.meta.FieldDescriptor;
import com.artemis.weaver.packed.PackedComponentWeaver;
import com.artemis.weaver.packed.PackedStubs;
import com.artemis.weaver.pooled.PooledComponentWeaver;

public class ComponentTypeTransmuter extends CallableTransmuter<Void> implements Opcodes {
	private ClassMetadata meta;
	private ClassReader cr;
	private ClassWriter cw;
	
	public ComponentTypeTransmuter(String file, ClassReader cr, ClassMetadata meta) {
		super(file);
		this.cr = cr;
		this.meta = meta;
	}
	
	@Override
	protected Void process(String file) throws FileNotFoundException, IOException {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor cv = cw;
		
		switch (meta.annotation) {
			case PACKED:
				validateOnlyPrimitives(meta.fields);
				cr = new AccessorGenerator(cr, meta).transform();
				cr = new PackedStubs(cr, meta).transform();
				cv = new CommonClassWeaver(cv, meta);
				cv = new PackedComponentWeaver(cv, meta);
				break;
			case POOLED:
				if (!meta.foundReset) {
					injectMethodStub("reset", "()V");
					cr.accept(cw, 0);
					cr = new ClassReader(cw.toByteArray());
					cv = cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				}
				cv = new CommonClassWeaver(cv, meta);
				cv = new PooledComponentWeaver(cv, meta);
				break;
			case NONE:
				return null;
			default:
				throw new IllegalArgumentException("Missing case: " + meta.annotation);
		}
		
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
	
	private void injectMethodStub(String name, String description) {
		MethodVisitor method = cw.visitMethod(ACC_PUBLIC, name, description, null, null);
		method.visitCode();
		method.visitLabel(new Label());
		method.visitInsn(RETURN);
		method.visitEnd();
	}
	
	private void validateOnlyPrimitives(List<FieldDescriptor> fields) {
		for (FieldDescriptor fd : fields) {
			if (fd.desc.length() > 1) {
				String error = String.format("%s: @PackedWeaver only works with primitive fields: %s",
					meta.type, fd);
				throw new WeaverException(error);
			}
		}
	}
}
