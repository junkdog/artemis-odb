## Change Log

#### Version: 1.0.0-SNAPSHOT
- New `ArtemisPlugin` API
- New `WorldConfigurationBuilder` convenience class.
- By convention, `@Wire` is now implied on all systems and managers. Use `@SkipWire` to suppress.
- `Manager` now treated as a system.
  - `Manager` is now part of the `BaseSystem` hierarchy.
  - EntityObserver supported on all systems.
- Removed deprecated methods and classes
  - Removed `@Mapper`
  - Removed UUID related accessors on int and World.
- `@PackedWeaver` marked as deprecated until performance issues have been resolved.


#### Version: 0.13.1 - 2015-09-20
- **Fix:**: Deleted entities no longer mess up subscriptions when edited in `SubscriptionListener#removed`.
- **Fix:**: Deleted entities retain components.


#### Version: 0.13.0 - 2015-09-14
- **BREAKING CHANGES**
  - `World#getEntity(int)` can return inactive Entities, check state using `int#isActive` or
     `EntitityManager.isActive(int)`.
  - `int.isActive` no longer works for checking whether an entity was just created, instead
    use `EntityManager#isNew(int)`.

- New additional serialization backend: [json via libgdx](https://github.com/junkdog/artemis-odb/wiki/libgdx-jso).
  - **GWT support**, works with libgdx's HTML5 backend.
  - Supports more libgdx classes out of the box.
- New `artemis-odb-debug` artifact
  - Replaces normal artemis-odb with this (remove artemis-odb from pom/gradle/IDE)
  - Performs extra runtime checks; tries to catch run-away flyweights.
  - Incurs a rather hefty performance penalty.
- **Fix**: SubscriptionListeners#removed ids resolved to a lot of nulls.


#### Version: 0.12.1 - 2015-09-05
- **Fix**: IntDeque resets beginIndex when growing.


#### Version: 0.12.0 - 2015-09-04
- InvocationStrategy configurable via WorldConfiguration. 
- Add @SkipWire annotation to exclude injection targets.
- Entities recycled in FIFO order.
- Change EntitySubscription order to deleted/added/changed.
- Change AspectSubscription order to removed/inserted.
- **FIX**: Resolved potential "markSupported() is undefined for the type InputStream" message when
  compiling for GWT.


#### Version: 0.11.4 - 2015-08-18
- **Fix**: Excessive object creation when serializing entities.
- **Fix**: Components referencing other entities are implicitly included when serializing.
- **Fix**: SaveFileFormat should only enumerate required components.


#### Version: 0.11.3 - 2015-08-14
- **Fix**: entity reference operations could fail when loading into a fresh World instance.


#### Version: 0.11.2 - 2015-08-11
- Added boolean `WorldSerializationManager#alwaysLoadStreamMemory`, forces fully loading the stream into
  memory during loading, even if stream reports that `mark` is supported. Necessary when working
  with libgdx's FileHandles.
  - Enabled by default.
- **Fix**: Duplicate ComponentNameComparator in json module, causing failing android builds.

#### Version: 0.11.1 - 2015-08-11
- **Fix**: Entities not registered as added to world *during* batch entity synchronization.


#### Version: 0.11.0 - 2015-08-10
- **BREAKING CHANGES**
  - EntitySubscription's inserted/removed method pass an IntBag
    of entity ids, instead of a Bag of entities.
  - EntityObserver's methods pass entity id.

- Optionally [customized injection](https://github.com/junkdog/artemis-odb/wiki/Customized-injection) strategies.
  - Injector caches class members per default.
- Serialize to json.
  - New [WorldSerializationManager](https://github.com/junkdog/artemis-odb/blob/master/artemis/src/main/java/com/artemis/managers/WorldSerializationManager.java).
  - Customize output by extending SaveFileFormat.
  - Uses [JsonBeans](https://github.com/EsotericSoftware/jsonbeans) behind the scenes.
- ComponentMappers now also sport get(int)/has(int) methods.
- int get/setUuid deprecated.
- Add getTag method to TagManager 
- Bag does equality comparisons, always.
- EntityManager counters stubbed and deprecated.
- **Fix**: UuidEntityManager deleted reused UUID's entity reference if it was created/deleted
  during the same tick.
- **Fix**: `@SafeVarargs` on Aspect - no more warnings.
- **Fix**: Replaced pooled components were not returned to pool.

#### Version: 0.10.2 - 2015-07-20
- **Fix**: Transmuters didn't resolve flyweight instances.


#### Version: 0.10.1 - 2015-07-20
- **Fix**: Reduced visibility of AspectionSubscriptionManager constructor.
- **Fix**: NPE related to `EntitySubscription#createSubscription`.


#### Version: 0.10.0 - 2015-07-19
- **MINOR BREAKING CHANGES**
  - BaseSystem is now the most basic system type.
    - World#getSystems returns Bag<BaseSystem>.
  - EntitySystem constructor expects Aspect.Builder:
    - Implies existing classes have to be recompiled against this release.
	- Existing custom entity systems need to pass Aspect.Builder to EntitySystem.
  - EntitySystem no longer implements EntityObserver.
  - ArtemisProfiler#initialize now requires BaseSystem instead of EntitySystem.
  - VoidEntitySystem no longer subclass of EntitySystem.
  - **`setSystem`** and **`setManager`** now under `WorldConfiguration`. This
    effectively removes `World#initialize`, as initializations happens inside
	World's constructor.

- New internal EntitySubscription and accompanying AspectSubscriptionManager
  resulting in cleaner and more flexible EntitySystems.
- SystemInvocationStrategy opening up for more granular control over system  processing.
  - Default strategy behaves as before.
- Aspect split into Aspect and Aspect.Builder.
  - Simplified static method names, mirroring Aspect's old instance methods.
- EntityFactories support for enum values.
- Children of EntitySystems can create flyweight entities.
- **Fix**: Uninitialized worlds threw cryptic NPE when invoking #process.
- **Fix**: `World#createFactory(Class<EntityFactory>)` was package local.
- **Fix**: `int#edit` used together with EntityTransmuters or Archetypes
  caused the wrong compositionId to be reported, if all changes took place
  during the same processing round..

#### Version: 0.9.0 - 2015-02-03
- **MINOR BREAKING CHANGES**
  - int instances should never be compared by reference equality; always do
    `entity1.equals(entity2)`.
  - It is no longer valid to add additional managers or systems to initialized worlds.

- MundaneWireException thrown by World#inject if class lacks @Wire.
  Previously, it silently failed to inject.
- Systems' entity iteration speed slightly faster.
- New EntityTransmuter class, an alternative but more efficient way of adding/removing
  components.
- Optimized entity removal speed. 
- Adding and removing existing entities from systems is now considerably faster.
- Add enablePackedWeaving to maven, gradle and CLI tool.
- Reduced EntitySystem memory footprint by 2/3.
- **Fix**: int systems can now subscribe to all entities when aspect is empty.
- **Fix**: EntityFactory annotation processor sometimes failed generating the
  factory implementation when running from within Eclipse.

#### Version: 0.8.1 - 2014-11-10
- CLI command for configuring eclipse projects with annotation processor (entity factories)
- **Fix**: Adding a component to an archetyped entity prior to it being added to the world
  resulted in the component never being registered.
  - Note that editing an entity created by an archetype voids the performance benefit of archetypes..

#### Version: 0.8.0 - 2014-10-20
- **MINOR BREAKING CHANGES**
  - Archetype moved to `com.artemis` package.

- Auto-generate [EntityFactories](https://github.com/junkdog/artemis-odb/wiki/EntityFactory).
- ArchetypeBuilder constructor accepts null passed as parent.

#### Version: 0.7.2 - 2014-10-08
- **Fix**: Chaining int#edit caused the internal bitset to reset
  between component removals.

#### Version: 0.7.1 - 2014-09-24
- Gradle plugin for bytecode weaving. See [Weave Automation](https://github.com/junkdog/artemis-odb/wiki/Weave-Automation)
- Faster removal of components.

#### Version: 0.7.0 - 2014-09-21

- **MINOR BREAKING CHANGES**
  - int state changes aren't reflected until the next system starts processing
    or a new `World#process` round begins, whichever comes first. 
  - Removed initial parameter (type) from Aspect methods; this changes the method
    descriptor, requiring any project to be recompiled.

- The GWT backend can now read values off annotations.
- **Bytecode optimizations:** (invoked via maven plugin or the CLI tool)
  - [Automatically optimize entity processing systems](https://github.com/junkdog/artemis-odb/wiki/Optimizing-int-Systems).
  - `@PackedWeaver` components no longer overwrite data in multi-World scenarios.
- Removed `artemis-benchmark`, refer to [entity-system-benchmarks](https://github.com/junkdog/entity-system-benchmarks) instead.
- It's no longer necessary to call `int#changedInWorld` and `int#addToWorld`
  - Use `int#edit` when adding or removing components.
- **Adding and removing entities to systems is now approximately 150% faster**.
  - int compositions are no longer primarily identified by BitSets, but instead
    have a compositionId - EntitySystems track which composition ids are of interest.
  - `ArchetypeBuilder` and `Archetype` precalculates compositionId, allowing for greatly increased
    insertion performance.
  - Optimized `EntitySystem#check`, entities are processed in bulk, less checks when removing entities,
    removed systemIndex.
- [@Profile](https://github.com/junkdog/artemis-odb/wiki/@Profile) entity systems with custom classes.
- New WorldConfiguration class
  - Set expected entity count
  - Limit number of rebuilt active entities per system/tick. Rebuilt indices ensure entities are
    processed in sequential order.
- New interface `PackedComponent.DisposedWithWorld` for freeing packed components' resources when
  disposing the world.
  - Automatically added to all `@PackedWeaver` components.
- `World#inject(Object)` can inject normal java classes with artemis types.
  - Requires that the injected object is annotated with `@Wire`
- Less bounds checking for PackedComponents.
- Added `World#getManagers` method.
- Lots of methods now deprecated
  - These will be removed when 1.0.0 is released.
  - See the `@deprecated` notice for how things work in the new world.
- maven plugin registered as "artemis-odb", ie `mvn artemis-odb:matrix`, or `mvn artemis-odb:artemis`
- **Fix**: `@PooledWeaver` wasn't resetting components.
  - Additionaly, only primitive fields are reset - objects are left intact.
- **Fix**: `EntityManager#getActiveEntityCount` could report the wrong number if entities were
  deleted before having been added to the world.
- **Fix**: Disabled entities are no longer added to systems.
- **Fix**: `EntityManager#isActive` could throw an OOB exception..
- **FIX**: Possible IOOB in `@PackedWeaver` components when accessing component data.

#### Version: 0.6.5 - 2014-07-17
- Better support for multiple concurrent worlds in regards to memory usage.
- Smaller entity instances.
- **Fix**: Actually fixed GWT support, bumped gwt to 2.6.0.

#### Version: 0.6.3 - 2014-07-07
- **Fix**: Potential IOOB exceptions in UuidEntityManager.

#### Version: 0.6.2 - 2014-07-07
- UUID is now optional for entities.
  - Add UuidEntityManager to automatically map UUID:s to entities.
  - UUID:s are lazily instantiated.
- **Fix**: GWT build was broken in 0.6.1.

#### Version: 0.6.1 - 2014-07-03
- New standalone `artemis-odb-cli` artifact:
  - Handles weaving (new in 0.6.1) and matrix generation.
  - Replaces `artemis-odb-matrix-X.Y.Z-cli.jar`.
- **Fix**: OOB Exception in ComponentManager with componentsByType when component types > 64.
- **Fix**: Calling `int#changedInWorld` before an entity is added to the world yields null.

#### Version: 0.6.0 - 2014-06-01
- **GWT support** ([demo](http://flet.github.io/spaceship-warrior-redux/)) sporting
  [libgdx](https://github.com/libgdx/libgdx)'s reflection wrapper code.
  - This means that `@Mapper` and `@Wire` works for GWT builds too.
  - Note: `@PooledWeaver` and `@PackedWeaver` don't work under GWT, though the presence
    of the annotations doesn't break anything.
- Automatically generate a bird's eye view of artemis: **[Component Dependency Matrix][CDM]**.
- **Faux structs** with [@PackedWeaver][Struct]`.
  - Looks and behaves just like normal java classes.
  - Direct field access works - no need to generate getters and setters - i.e. `position.x += 0.24f` is valid.
  - Contiguously stored in memory, internally backed by a shared `ByteBuffer.allocateDirect()`.
  - Works for all components composed of primitive types.
- int systems and managers can `@Wire` (inject) anything from the world: will eventually
  replace `@Mapper`. No need to annotate each field - just annotate the class and artemis
  will take care of injecting applicable fields.
  - `@Wire` can inject parent classes too.
  - `@Wire` can resolve non-final references. Eg, AbstractEntityFactory is resolved as
    EntityFactory etc. See [test/example](https://github.com/junkdog/artemis-odb/blob/6eb51ccc7a72a4ff16737277f609a58f9cae94ca/artemis/src/test/java/com/artemis/SmarterWireTest.java#L39).
- EntitySystems process entities in ascending id order.
  - Considerably faster processing when memory access is aligned (potential biggest gains from
    PackedComponents).
  - Slightly slower insert/remove (3-4% with 4096 entities)
- New optional `UuidEntityManager`, tracks entities by UUID.
- Optional `expectedEntityCount` parameters in `World` constructor.
- `-DideFriendlyPacking`: If true, will leave field stubs to keep IDE:s 
  happy after transformations. Defaults to false.
- `-DenablePooledWeaving`: Enables weaving of pooled components (more viable on
  Android than JVM). Defaults to true.
- `-DenableArtemisPlugin`: If set to false, no weaving will take place (useful
  for debugging).
- **Fix**: Possible NPE when removing recently created entities.
- **Fix**: `int#getComponent` would sometimes throw an `ArrayIndexOutOfBoundsException`.

#### Version: 0.5.0 - 2013-11-24
 - Changed artemis to a multi-module project (the `artemis` folder is the old root).
 - int instances are recycled.
 - New component types, `PooledComponent` and `PackedComponent`.
   - Optionally transform components with `@PackedWeaver` and `@PooledWeaver` by
     configuring the `artemis-odb-maven-plugin`.
 - New method `int#createComponent(Class<Component>)`.
 - Annotation processor validates pooled and packed component types.
 - Managers support `@Mapper` annotation.
 - No longer necessary to stub `Manager#initialize()`.
 - `GroupManager#getGroups` returns an empty bag if entity isn't in any group.
 - `World#dispose` for disposing managers and systems with managed resources.
 - **Fix**: DelayedEntityProcessingSystem prematurely expired entities.
 - **Fix**: Recycled entities would sometimes have their components cleared when
   recycled during the same round as the original entity was deleted.
 - **Fix**: GroupManager avoids duplicate entities and removes them upon deletion.

 [CDM]: https://github.com/junkdog/artemis-odb/wiki/Component-Dependency-Matrix
 [Struct]: https://github.com/junkdog/artemis-odb/wiki/Packed-Weaver
