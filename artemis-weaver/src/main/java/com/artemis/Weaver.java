package com.artemis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.objectweb.asm.ClassReader;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.WeaverType;
import com.artemis.meta.MetaScanner;

public class Weaver {

	private static void processClass(ExecutorService threadPool, String file, List<ClassMetadata> processed) {
		
		FileInputStream stream = null;
		try
		{
			ClassReader cr = classReaderFor(file);
			ClassMetadata meta = scan(cr);
			
			if (meta.annotation == WeaverType.NONE || meta.isPreviouslyProcessed)
				return;

			switch (meta.annotation) {
				case PACKED:
//					threadPool.submit(new PackedWeaver());
					break;
				case POOLED:
//					threadPool.submit(new PooledWeaver());
					break;
				default:
					throw new UnsupportedOperationException("Missing annotation case: " + meta.annotation);
			}
			processed.add(meta);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
