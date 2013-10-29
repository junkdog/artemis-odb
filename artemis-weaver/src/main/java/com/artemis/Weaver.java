package com.artemis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.MetaScanner;

public class Weaver {

	private static void processClass(ExecutorService threadPool, String file, List<ClassMetadata> processed) {
		
		FileInputStream stream = null;
//		try
//		{
//			stream = new FileInputStream(file)
//			ClassReader cr = new ClassReader(stream);
//			ArtemisConfigurationData meta = scan(cr);
			
//			meta.current = Type.getObjectType(cr.getClassName());
//			
//			if (meta.isPreviouslyProcessed)
//				return;
//			
//			if (meta.isSystemAnnotation || meta.profilingEnabled)
//			{
//				threadPool.submit(new SystemWeaver(file, cr, meta));
//				processed.add(meta);
//			}
//			else if (meta.isManagerAnnotation)
//			{
//				threadPool.submit(new ManagerWeaver(file, cr, meta));
//				processed.add(meta);
//			}
//		}
//		catch (FileNotFoundException e)
//		{
//			System.err.println("not found: " + file);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
	}
	
	static ClassReader classReaderFor(InputStream file) {
		try {
			return new ClassReader(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static ClassReader classReaderFor(String file) {
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			return classReaderFor(stream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ClassMetadata scan(ClassReader source) {
		ClassMetadata info = new ClassMetadata();
		source.accept(new MetaScanner(info), 0);
		return info;
	}
}
