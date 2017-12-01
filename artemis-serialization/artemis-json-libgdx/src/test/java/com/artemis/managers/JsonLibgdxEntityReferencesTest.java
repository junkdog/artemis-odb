package com.artemis.managers;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.component.LevelState;
import com.artemis.component.ParentedPosition;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.io.SaveFileFormat;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@Wire(injectInherited = true)
public class JsonLibgdxEntityReferencesTest extends AbstractEntityReferencesTest {
    public JsonLibgdxEntityReferencesTest() {
        super("/level_3.json");
    }
    @Override
    protected WorldSerializationManager.ArtemisSerializer<?> createBackend(World world) {
        JsonArtemisSerializer backend = new JsonArtemisSerializer(world);
        backend.prettyPrint(true);
        return backend;
    }
}
