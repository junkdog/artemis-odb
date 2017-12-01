package com.artemis.managers;

import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.io.JsonArtemisSerializer;
import org.junit.Test;

@Wire(failOnNull = false, injectInherited = true)
public class JsonLibGdxWorldSerializationManagerTest extends AbstractWorldSerializationManagerTest {

	@Test /* https://github.com/junkdog/artemis-odb/issues/452 */
	public void save_compact_json() throws Exception {
		((JsonArtemisSerializer)backend).prettyPrint(false);
		save(allEntities.getEntities());
	}

	@Override
	protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
		JsonArtemisSerializer result = new JsonArtemisSerializer(world);
		result.prettyPrint(true);
		return result;
	}
}
