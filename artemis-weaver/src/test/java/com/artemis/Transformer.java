package com.artemis;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import com.artemis.meta.ClassMetadata;
import com.artemis.weaver.ComponentTypeTransmuter;

final class Transformer {
	
	private Transformer() {}
	
	static ClassMetadata transform(Class<?> klazz) throws Exception {
		InputStream classStream = klazz.getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class");
		ClassReader cr = Weaver.classReaderFor(classStream);
		ClassMetadata meta = Weaver.scan(cr);
		meta.type = Type.getObjectType(cr.getClassName());
		
		ComponentTypeTransmuter weaver = new ComponentTypeTransmuter(null, cr, meta);
		weaver.call();
		
		ClassWriter cw = weaver.getClassWriter();
		assertEquals("", ClassUtil.verifyClass(cw));
		
		classStream.close();
		
		return Weaver.scan(new ClassReader(cw.toByteArray()));
	}
}

