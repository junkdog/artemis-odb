package com.artemis.io;

import com.artemis.*;
import com.artemis.annotations.Transient;
import com.artemis.annotations.Wire;
import com.artemis.components.SerializationTag;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.artemis.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

@Wire(failOnNull = false)
public class EntitySerializer implements Json.Serializer<Entity> {

	private final Bag<Component> components = new Bag<Component>();
	private final ComponentNameComparator comparator = new ComponentNameComparator();
	private final World world;
	private final ReferenceTracker referenceTracker;

	private GroupManager groupManager;
	private TagManager tagManager;
	private final Collection<String> registeredTags;

	private Archetype emptyEntity;
	private boolean isSerializingEntity;

	private ComponentMapper<SerializationTag> saveTagMapper;

	SerializationKeyTracker keyTracker;
	ArchetypeMapper archetypeMapper;

	Map<String, Class<? extends Component>> types = new HashMap<String, Class<? extends Component>>();
	private IdentityHashMap<Class<? extends Component>, String> lookupMap;

	private int archetype = -1;

	public EntitySerializer(World world, ReferenceTracker referenceTracker) {
		this.world = world;
		this.emptyEntity = new ArchetypeBuilder().build(world);
		this.referenceTracker = referenceTracker;
		world.inject(this);

		registeredTags = (tagManager != null)
			? tagManager.getRegisteredTags()
			: Collections.EMPTY_LIST;
	}

	void preLoad() {
		keyTracker = new SerializationKeyTracker();
	}

	void preWrite(SaveFileFormat save) {
		lookupMap =	save.componentIdentifiers;
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
		for (int i = 0, s = components.size(); s > i; i++) {
			Component c = components.get(i);
			if (ClassReflection.getDeclaredAnnotation(c.getClass(), Transient.class) != null)
				continue;

			String componentIdentifier = lookupMap.get(c.getClass());
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
		for (String tag : registeredTags) {
			if (tagManager.getEntity(tag) != e)
				continue;

			json.writeValue("tag", tag);
			break;
		}
	}

	private void writeKeyTag(Json json, Entity e) {
		if (saveTagMapper.has(e)) {
			String key = saveTagMapper.get(e).tag;
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

		Entity e = world.createEntity(emptyEntity);

		jsonData = readArchetype(jsonData, e);
		jsonData = readTag(jsonData, e);
		jsonData = readKeyTag(jsonData, e);
		jsonData = readGroups(jsonData, e);

		// when we deserialize a single entity
		if (!"components".equals(jsonData.name()))
			jsonData = jsonData.child;

		assert("components".equals(jsonData.name));
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
		archetypeMapper.transmute(e, archetype);
		while (component != null) {
			assert (component.name() != null);
			Class<? extends Component> componentType = types.get(component.name);
			readComponent(json, component, e.getComponent(componentType));

			component = component.next;
		}
	}

	private void readComponentsEdit(Json json, Entity e, JsonValue component) {
		EntityEdit edit = e.edit();
		while (component != null) {
			assert (component.name() != null);
			Class<? extends Component> componentType = types.get(component.name);
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
			saveTagMapper.create(e).tag = key;
			jsonData = jsonData.next;
		}

		return jsonData;
	}
}
