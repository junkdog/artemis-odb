package com.artemis.meta;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.component.ComponentToWeave;
import com.artemis.component.PooledComponentNotScanned;
import com.artemis.component.PooledComponentWithReset;
import com.artemis.meta.ClassMetadata.WeaverType;

public class MetaScannerTest {
	
	private World world;

	@Before
	public void setup() {
		world = new World();
		world.initialize();
	}
	
	@Test
	public void pooled_component_scanning() throws Exception {
		Entity e1 = world.createEntity();
		ComponentToWeave c1a = e1.createComponent(ComponentToWeave.class);
		PooledComponentWithReset c1b = e1.createComponent(PooledComponentWithReset.class);
		PooledComponentNotScanned c1c = e1.createComponent(PooledComponentNotScanned.class);
		e1.addToWorld();
		
		ClassMetadata scan1 = scan(ComponentToWeave.class);
		ClassMetadata scan2 = scan(PooledComponentWithReset.class);
		ClassMetadata scan3 = scan(PooledComponentNotScanned.class);
		
		assertEquals(false, scan1.foundReset);
		assertEquals(false, scan1.foundEntityFor);
		assertEquals(WeaverType.POOLED, scan1.annotation);
		assertEquals(false, scan1.isPreviouslyProcessed);
		
		assertEquals(true, scan2.foundReset);
		assertEquals(false, scan2.foundEntityFor);
		assertEquals(WeaverType.POOLED, scan2.annotation);
		assertEquals(false, scan2.isPreviouslyProcessed);
		
		assertEquals(WeaverType.NONE, scan3.annotation);
	}
	
	static ClassMetadata scan(Class<?> klazz) throws Exception {
		String classResource = "/" + klazz.getName().replace('.', '/') + ".class";
		
		InputStream stream = MetaScannerTest.class.getResourceAsStream(classResource);
		ClassReader cr = new ClassReader(stream);
		ClassMetadata info = new ClassMetadata();
		info.type = Type.getObjectType(cr.getClassName());
		cr.accept(new MetaScanner(info), 0);
		stream.close();
		return info;
	}
}
