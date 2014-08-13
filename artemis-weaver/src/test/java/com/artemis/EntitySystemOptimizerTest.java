package com.artemis;

import static com.artemis.Transformer.transform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.GlobalConfiguration;
import com.artemis.system.BeginEndSystem;
import com.artemis.system.NoBeginEndSystem;
import com.artemis.system.PoorFellowSystem;

@SuppressWarnings("static-method")
public class EntitySystemOptimizerTest {

	@Before
	public void init() {
		GlobalConfiguration.optimizeEntitySystems = true;
	}
	
	@Test
	public void only_valid_entity_systems_test() {
		assertFalse(scan(BeginEndSystem.class).isOptimizableSystem);
		assertFalse(scan(NoBeginEndSystem.class).isOptimizableSystem);
		assertTrue(scan(PoorFellowSystem.class).isOptimizableSystem);
	}
	
	@Test
	public void validate_optimized_system_test() throws Exception {
		ClassMetadata meta = Weaver.scan(transform(PoorFellowSystem.class));
		
		assertEquals("com/artemis/EntitySystem", meta.superClass);		
		assertFalse(meta.isOptimizableSystem); 
	}
	
	private static ClassMetadata scan(Class<?> klazz) {
		InputStream classStream = klazz.getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class");
		ClassReader cr = Weaver.classReaderFor(classStream);
		ClassMetadata meta = Weaver.scan(cr);
		meta.type = Type.getObjectType(cr.getClassName());
		
		return meta;
	}
}
