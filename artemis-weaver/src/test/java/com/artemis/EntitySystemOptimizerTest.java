package com.artemis;

import static com.artemis.Transformer.transform;
import static com.artemis.meta.ClassMetadata.OptimizationType.FULL;
import static com.artemis.meta.ClassMetadata.OptimizationType.NOT_OPTIMIZABLE;
import static com.artemis.meta.ClassMetadata.OptimizationType.SAFE;
import static org.junit.Assert.assertEquals;

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
import com.artemis.system.SafeOptimizeSystem;

@SuppressWarnings("static-method")
public class EntitySystemOptimizerTest {

	@Before
	public void init() {
		GlobalConfiguration.optimizeEntitySystems = true;
	}
	
	@Test
	public void only_valid_entity_systems_test() {
		assertEquals(NOT_OPTIMIZABLE, scan(BeginEndSystem.class).sysetemOptimizable);
		assertEquals(NOT_OPTIMIZABLE, scan(NoBeginEndSystem.class).sysetemOptimizable);
		assertEquals(FULL, scan(PoorFellowSystem.class).sysetemOptimizable);
	}
	
	@Test
	public void validate_optimized_system_test() throws Exception {
		ClassMetadata meta = Weaver.scan(transform(PoorFellowSystem.class));
		
		assertEquals("com/artemis/EntitySystem", meta.superClass);		
		assertEquals(NOT_OPTIMIZABLE, meta.sysetemOptimizable); 
	}
	
	@Test
	public void detect_preserve_process_visibility_test() throws Exception {
		ClassMetadata meta = scan(SafeOptimizeSystem.class);
		assertEquals(SAFE, meta.sysetemOptimizable); 
	}
	
	private static ClassMetadata scan(Class<?> klazz) {
		InputStream classStream = klazz.getResourceAsStream("/" + klazz.getName().replace('.', '/') + ".class");
		ClassReader cr = Weaver.classReaderFor(classStream);
		ClassMetadata meta = Weaver.scan(cr);
		meta.type = Type.getObjectType(cr.getClassName());
		
		return meta;
	}
}
