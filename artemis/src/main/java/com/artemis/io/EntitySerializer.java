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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

@Wire(failOnNull = false)
public class EntitySerializer implements Json.Serializer<Entity> {

	private final Bag<Component> components = new Bag<Component>();
	private final ComponentNameComparator comparator = new ComponentNameComparator();
	private final World world;

	private GroupManager groupManager;
	private TagManager tagManager;
	private final Collection<String> registeredTags;


	private final ObjectMap<String, Class<? extends Component>> componentClasses;

	public EntitySerializer(World world) {
		this.world = world;
		world.inject(this);

		componentClasses = new ObjectMap<String, Class<? extends Component>>();

		registeredTags = (tagManager != null)
			? tagManager.getRegisteredTags()
			: Collections.EMPTY_LIST;
	}

	@Override
	public void write(Json json, Entity e, Class knownType) {
		ComponentLookupSerializer lookup =
				(ComponentLookupSerializer) json.getSerializer(IdentityHashMap.class);
		IdentityHashMap<Class<? extends Component>, String> componentMap = lookup.classToIdentifierMap();

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

			String componentIdentifier = componentMap.get(c.getClass());
			json.writeObjectStart(componentIdentifier);
			json.writeFields(c);
			json.writeObjectEnd();
		}
		json.writeObjectEnd();
		json.writeObjectEnd();

		components.clear();
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
		ComponentLookupSerializer lookup =
				(ComponentLookupSerializer) json.getSerializer(IdentityHashMap.class);
		Map<String, Class<? extends Component>> components = lookup.identifierToClassMap();

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

			Class<? extends Component> componentType = components.get(component.name);
			Component c = edit.create(componentType);
			json.readFields(c, component);

			component = component.next;
		}

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
