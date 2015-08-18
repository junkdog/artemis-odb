package com.artemis.io;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.artemis.annotations.Transient;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.esotericsoftware.jsonbeans.ObjectMap;

import java.util.*;

@Wire(failOnNull = false)
public class EntitySerializer implements JsonSerializer<Entity> {

	private final Bag<Component> components = new Bag<Component>();
	private final ComponentNameComparator comparator = new ComponentNameComparator();
	private final World world;
	private final ReferenceTracker referenceTracker;

	private GroupManager groupManager;
	private TagManager tagManager;
	private final Collection<String> registeredTags;

	private final ObjectMap<String, Class<? extends Component>> componentClasses;

	private boolean isSerializingEntity;


	Map<String, Class<? extends Component>> types = new HashMap<String, Class<? extends Component>>();
	private IdentityHashMap<Class<? extends Component>, String> lookupMap;

	public EntitySerializer(World world, ReferenceTracker referenceTracker) {
		this.world = world;
		this.referenceTracker = referenceTracker;
		world.inject(this);

		componentClasses = new ObjectMap<String, Class<? extends Component>>();
		registeredTags = (tagManager != null)
			? tagManager.getRegisteredTags()
			: Collections.EMPTY_LIST;
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
			json.writeValue(e.id);
			return;
		} else {
			isSerializingEntity = true;
		}

		world.getComponentManager().getComponentsFor(e, components);
		components.sort(comparator);

		json.writeObjectStart();
		writeTag(json, e);
		writeGroups(json, e);

		json.writeObjectStart("components");
		for (int i = 0, s = components.size(); s > i; i++) {
			Component c = components.get(i);
			if (c.getClass().getAnnotation(Transient.class) != null)
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

	private ComponentLookupSerializer componentLookup(Json json) {
		return (ComponentLookupSerializer) json.getSerializer(IdentityHashMap.class);
	}

	private void writeTag(Json json, Entity e) {
		for (String tag : registeredTags) {
			if (tagManager.getEntity(tag) != e)
				continue;

			json.writeValue("tag", tag);
			break;
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
			Entity entity = Entity.createFlyweight(world);
			entity.id = entityId;
			return entity;
		} else {
			isSerializingEntity = true;
		}

		Entity e = world.createEntity();

		jsonData = readTag(jsonData, e);
		jsonData = readGroups(jsonData, e);

		// when we deserialize a single entity
		if (!"components".equals(jsonData.name()))
			jsonData = jsonData.child;

		assert("components".equals(jsonData.name));
		JsonValue component = jsonData.child;

		EntityEdit edit = e.edit();
		while (component != null) {
			assert(component.name() != null);
			Class<? extends Component> componentType = types.get(component.name);
			Component c = edit.create(componentType);
			json.readFields(c, component);

			// if component contains entity references, add
			// entity reference operations
			referenceTracker.addEntityReferencingComponent(c);

			component = component.next;
		}

		isSerializingEntity = false;

		return edit.getEntity();
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

	private JsonValue readTag(JsonValue jsonData, Entity e) {
		if ("tag".equals(jsonData.name)) {
			tagManager.register(jsonData.asString(), e);
			jsonData = jsonData.next;
		}

		return jsonData;
	}
}
