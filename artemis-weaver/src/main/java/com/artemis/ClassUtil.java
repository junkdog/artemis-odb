package com.artemis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

public final class ClassUtil implements Opcodes
{
	private ClassUtil() {}

	public static void injectMethodStub(ClassWriter cw, String methodName) {
		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED, methodName, "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitEnd();
	}
	
	public static void injectAnnotation(ClassWriter cw, String desc) {
		AnnotationVisitor av = cw.visitAnnotation(desc, true);
		av.visitEnd();
	}
	
	public static void writeClass(ClassWriter writer, String file) {
		FileOutputStream fos = null;
		try	{
			fos = new FileOutputStream(file);
			fos.write(writer.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String verifyClass(ClassWriter writer) {
//		cr.accept(cw, 0);
    	StringWriter sw = new StringWriter();
		PrintWriter printer = new PrintWriter(sw);
    	
    	CheckClassAdapter.verify(new ClassReader(writer.toByteArray()), false, printer);
    	
    	return sw.toString();
	}
}
