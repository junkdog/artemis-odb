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
	private TagManager tagManager;
	private GroupManager groupManager;

	private final ObjectMap<String, Class<? extends Component>> componentClasses;

	public EntitySerializer(World world) {
		this.world = world;
		world.inject(this);

		componentClasses = new ObjectMap<String, Class<? extends Component>>();
	}

	@Override
	public void write(Json json, Entity e, Class knownType) {
		ComponentLookupSerializer lookup =
				(ComponentLookupSerializer) json.getSerializer(IdentityHashMap.class);
		IdentityHashMap<Class<? extends Component>, String> componentMap = lookup.classToIdentifierMap();

		Collection<String> registeredTags = (tagManager != null)
			? tagManager.getRegisteredTags()
			: Collections.EMPTY_LIST;

		world.getComponentManager().getComponentsFor(e, components);
		components.sort(comparator);

		json.writeObjectStart();
		if (registeredTags.size() > 0)
			writeTagIfExists(json, e, registeredTags);

		json.writeArrayStart("components");
		for (int i = 0, s = components.size(); s > i; i++) {
			Component c = components.get(i);
			if (c.getClass().getAnnotation(Transient.class) != null)
				continue;

			String componentIdentifier = componentMap.get(c.getClass());
			json.writeObjectStart();
			json.writeObjectStart(componentIdentifier);
			json.writeFields(c);
			json.writeObjectEnd();
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		json.writeObjectEnd();

		components.clear();
	}

	private void writeTagIfExists(Json json, Entity e, Collection<String> tags) {
		for (String tag : tags) {
			if (tagManager.getEntity(tag) != e)
				continue;

			json.writeValue("tag", tag);
			break;
		}
	}

	@Override
	public Entity read(Json json, JsonValue jsonData, Class type) {
		ComponentLookupSerializer lookup =
				(ComponentLookupSerializer) json.getSerializer(IdentityHashMap.class);
		Map<String, Class<? extends Component>> components = lookup.identifierToClassMap();

		EntityEdit ee = world.createEntity().edit();
//		JsonValue tag = jsonData.get("tag");
//		if (tag != null)
//			tagManager.register(tag.asString(), ee.getEntity());

//		JsonValue component = jsonData.getChild("components");
		JsonValue component = jsonData.child;
		while (component != null) {
			assert(component.child.name() != null);

			Class<? extends Component> componentType = components.get(component.child.name);
			Component c = ee.create(componentType);
			json.readFields(c, component.child);

			component = component.next;
		}


		return ee.getEntity();
	}

	private Class<? extends Component> getClass(String componentClass) {
		Class<? extends Component> c = componentClasses.get(componentClass);
		if (c == null) {
			try {
				c = (Class<? extends Component>)Class.forName(componentClass);
				componentClasses.put(componentClass, c);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		return c;
	}
}
