package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.SkipWire;
import com.artemis.annotations.Wire;
import com.artemis.borrowed.IntMap;
import com.artemis.components.SerializationTag;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;

import java.util.*;

@Wire(failOnNull = false)
public class EntitySerializer implements JsonSerializer<Entity> {

    private final Bag<Component> components = new Bag<Component>();
    private final ComponentNameComparator comparator = new ComponentNameComparator();
    @SkipWire
    private final World world;
    private final ReferenceTracker referenceTracker;
    private final DefaultObjectStore defaultValues;
    final EntityPoolFactory factory;

    private GroupManager groupManager;
    private TagManager tagManager;

    private boolean isSerializingEntity;

    private ComponentMapper<SerializationTag> saveTagMapper;

    SerializationKeyTracker keyTracker;
    ArchetypeMapper archetypeMapper;
    SaveFileFormat serializationState;

    private int archetype = -1;

    public EntitySerializer(World world, ReferenceTracker referenceTracker) {
        this.world = world;
        this.referenceTracker = referenceTracker;
        defaultValues = new DefaultObjectStore();
        factory = new EntityPoolFactory(world);
        world.inject(this);
    }

    void setUsePrototypes(boolean usePrototypes) {
        defaultValues.setUsePrototypes(usePrototypes);
    }

    void preLoad() {
        keyTracker = new SerializationKeyTracker();
    }

    @Override
    public void write(Json json, Entity e, Class knownType) {
        // need to track this in case the components of an entity
        // reference another entity - if so, we only want to record
        // the id
        if (isSerializingEntity) {
            json.writeValue(e.getId());
            return;
        } else {
            isSerializingEntity = true;
        }

        world.getComponentManager().getComponentsFor(e.getId(), components);
        components.sort(comparator);

        json.writeObjectStart();
        writeArchetype(json, e);
        writeTag(json, e);
        writeKeyTag(json, e);
        writeGroups(json, e);

        json.writeObjectStart("components");
        SaveFileFormat.ComponentIdentifiers identifiers = serializationState.componentIdentifiers;
        Map<Class<? extends Component>, String> typeToName = identifiers.typeToName;

        for (int i = 0, s = components.size(); s > i; i++) {
            Component c = components.get(i);
            if (identifiers.isTransient(c.getClass()))
                continue;

            if (defaultValues.hasDefaultValues(c))
                continue;

            String componentIdentifier = typeToName.get(c.getClass());
            json.writeObjectStart(componentIdentifier);

            json.writeFields(c);
            json.writeObjectEnd();
        }
        json.writeObjectEnd();
        json.writeObjectEnd();

        components.clear();

        isSerializingEntity = false;
    }

    private void writeArchetype(Json json, Entity e) {
        json.writeValue("archetype", e.getCompositionId());
    }

    private void writeTag(Json json, Entity e) {
        if (tagManager != null) {
            final IntMap.Values<String> registeredTags = tagManager.getRegisteredTags();
            for (String tag : registeredTags) {
                if (tagManager.getEntityId(tag) != e.getId())
                    continue;

                json.writeValue("tag", tag);
                break;
            }
        }

    }

    private void writeKeyTag(Json json, Entity e) {
        if (saveTagMapper.has(e.getId())) {
            String key = saveTagMapper.get(e.getId()).tag;
            if (key != null)
                json.writeValue("key", key);
        }
    }

    private void writeGroups(Json json, Entity e) {
        if (groupManager == null)
            return;

        ImmutableBag<String> groups = groupManager.getGroups(e);
        if (groups.size() == 0)
            return;

        json.writeArrayStart("groups");
        for (String group : groups) {
            json.writeValue(group);
        }
        json.writeArrayEnd();
    }

    @Override
    public Entity read(Json json, JsonValue jsonData, Class type) {
        // need to track this in case the components of an entity
        // reference another entity - if so, we only want to read
        // the id
        if (isSerializingEntity) {
            int entityId = json.readValue(Integer.class, jsonData);
            // creating a temporary entity; this will later be translated
            // to the correct entity
            return FakeEntityFactory.create(world, entityId);
        } else {
            isSerializingEntity = true;
        }

        Entity e = factory.createEntity();

        jsonData = readArchetype(jsonData, e);
        jsonData = readTag(jsonData, e);
        jsonData = readKeyTag(jsonData, e);
        jsonData = readGroups(jsonData, e);

        // when we deserialize a single entity
        if (!"components".equals(jsonData.name()))
            jsonData = jsonData.child;

        assert ("components".equals(jsonData.name));
        JsonValue component = jsonData.child;

        if (archetype != -1) {
            readComponentsArchetype(json, e, component);
        } else {
            readComponentsEdit(json, e, component);
        }

        isSerializingEntity = false;

        return e;
    }

    private void readComponentsArchetype(Json json, Entity e, JsonValue component) {
        SaveFileFormat.ComponentIdentifiers identifiers = serializationState.componentIdentifiers;

        archetypeMapper.transmute(e, archetype);
        while (component != null) {
            assert (component.name() != null);
            Class<? extends Component> componentType = identifiers.getType(component.name);
            readComponent(json, component, e.getComponent(componentType));

            component = component.next;
        }
    }

    private void readComponentsEdit(Json json, Entity e, JsonValue component) {
        SaveFileFormat.ComponentIdentifiers identifiers = serializationState.componentIdentifiers;

        EntityEdit edit = e.edit();
        while (component != null) {
            assert (component.name() != null);
            Class<? extends Component> componentType = identifiers.getType(component.name);
            readComponent(json, component, edit.create(componentType));

            component = component.next;
        }
    }

    private void readComponent(Json json, JsonValue component, Component c) {
        json.readFields(c, component);

        // if component contains entity references, add
        // entity reference operations
        referenceTracker.addEntityReferencingComponent(c);
    }

    private JsonValue readGroups(JsonValue jsonData, Entity e) {
        if ("groups".equals(jsonData.name)) {
            JsonValue group = jsonData.child;
            while (group != null) {
                groupManager.add(e, group.asString());
                group = group.next;
            }

            jsonData = jsonData.next;
        }

        return jsonData;
    }

    private JsonValue readArchetype(JsonValue jsonData, Entity e) {
        // archetypes is optional, to avoid breaking compatibility
        if ("archetype".equals(jsonData.name)) {
            archetype = jsonData.asInt();
            jsonData = jsonData.next;
        } else {
            archetype = -1;
        }

        return jsonData;
    }

    private JsonValue readTag(JsonValue jsonData, Entity e) {
        if ("tag".equals(jsonData.name)) {
            tagManager.register(jsonData.asString(), e);
            jsonData = jsonData.next;
        }

        return jsonData;
    }

    private JsonValue readKeyTag(JsonValue jsonData, Entity e) {
        if ("key".equals(jsonData.name)) {
            String key = jsonData.asString();
            keyTracker.register(key, e);
            saveTagMapper.create(e.getId()).tag = key;
            jsonData = jsonData.next;
        }

        return jsonData;
    }
}
