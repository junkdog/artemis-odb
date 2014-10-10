package ${model.packageName};

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.Bag;
<#list model.getComponents(true) as component>
import ${component};
</#list>

@Wire(failOnNull=false)
public class ${model.factoryName}Impl implements ${model.factoryName} {
	// default fields
	private World world;
	private boolean _tag;
	private String _tag_tag;
	private boolean _group;
	private Bag<String> _groups = new Bag<String>();
	
	boolean _sealed;
	
	private TagManager tagManager;
	private GroupManager groupManager;
	private Archetype archetype;
	
	// component parameter fields
<#list model.fields as field>
	${field};
</#list>
	
	// mappers
<#list model.mappedComponents as component>
	private ComponentMapper<${component}> ${component?uncap_first}Mapper;
</#list>
	
	public ${model.factoryName}Impl(World world) {
		this.world = world;
		world.inject(this);
		
		archetype = new ArchetypeBuilder()
<#list model.getComponents(false) as component>
			.add(${component}.class)
</#list>
			.build(world);
	}
	
	private ${model.factoryName}Impl() {}
	
	@Override
	public ${model.factoryName} copy() {
		${model.factoryName}Impl copy = new ${model.factoryName}Impl();
		copy.world = world;
		copy.archetype = archetype;
<#list model.stickyMethods as m>
	<#list m.params as param>
		copy.${param.field} = ${param.field};
	</#list>
</#list>
		
		world.inject(copy);
		
		return copy;
	}

	@Override
	public ${model.factoryName} tag(String tag) {
		_tag = true;
		this._tag_tag = tag;
		return this;
	}

	@Override
	public ${model.factoryName} group(String group) {
		_group = true;
		_groups.add(group);
		return this;
	}

	@Override
	public ${model.factoryName} group(String groupA, String... groups) {
		_groups.add(groupA);
		for (int i = 0; groups.length > i; i++) {
			_groups.add(groups[i]);
		}
		return this;
	}

	@Override
	public ${model.factoryName} group(String groupA, String groupB, String... groups) {
		_group = true;
		_groups.add(groupA);
		_groups.add(groupB);
		for (int i = 0; groups.length > i; i++) {
			_groups.add(groups[i]);
		}
		return this;
	}
	
	@Override
	public Entity create() {
		_sealed = true;
		
		Entity e = world.createEntity(archetype);

<#list model.instanceMethods as m>
		if (${m.flagName}) {
			${m.componentName} c = ${m.componentName?uncap_first}Mapper.get(e);
	<#list m.params as param>
			c.${param.param} = ${param.field};
	</#list>
			${m.flagName} = false;
		}
		
</#list>
<#list model.stickyMethods as m>
		{
			${m.componentName} c = ${m.componentName?uncap_first}Mapper.get(e);
	<#list m.params as param>
			c.${param.param} = ${param.field};
	</#list>
		}
		
</#list>
<#list model.setterMethods as m>
		if (${m.flagName}) {
			${m.componentName} c = ${m.componentName?uncap_first}Mapper.get(e);
			c.${m.setter}(${m.paramArgs});
			${m.flagName} = false;
		}
		
</#list>
		if (_tag) {
			tagManager.register(_tag_tag, e);
			_tag = false;
		}
		
		if (_group) {
			for (int i = 0, s = _groups.size(); s > i; i++) {
				groupManager.add(e, _groups.get(i));
			}
			_group = false;
		}
		
		return e;
	}


<#list model.stickyMethods as m>
	@Override
	public ${model.factoryName} ${m.name}(${m.paramsFull}) {
		if (_sealed) {
			String err = "${m.name} are stickied, unable to change after creating " +
					"first entity. See copy().";
			throw new IllegalArgumentException(err);
		}
<#list m.params as param>
		${param.field} = ${param.param};
</#list>
		return this;
	}

</#list>
<#list model.instanceMethods as m>	
	@Override
	public ${model.factoryName} ${m.name}(${m.paramsFull}) {
		${m.flagName} = true;
<#list m.params as param>
		${param.field} = ${param.param};
</#list>
		return this;
	}
</#list>
<#list model.setterMethods as m>	
	@Override
	public ${model.factoryName} ${m.name}(${m.paramsFull}) {
		${m.flagName} = true;
<#list m.params as param>
		${param.field} = ${param.param};
</#list>
		return this;
	}
</#list>	
}