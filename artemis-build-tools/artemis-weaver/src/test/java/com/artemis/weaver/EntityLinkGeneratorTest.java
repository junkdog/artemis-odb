package com.artemis.weaver;

import com.artemis.Weaver;
import com.artemis.component.EntityReferencing;
import com.artemis.meta.ClassMetadata;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

public class EntityLinkGeneratorTest {
	@Test
	public void generate_entity_id_accessor() {
		ClassReader cr = Weaver.toClassReader(EntityReferencing.class);
		ClassMetadata meta = Weaver.scan(EntityReferencing.class);
		EntityLinkGenerator elg = new EntityLinkGenerator(null, cr, meta);

		elg.process("EntityReferencing.class");
	}
}