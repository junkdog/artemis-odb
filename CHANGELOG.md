# Change Log

## Version: 0.6.6-SNAPSHOT

- **MINOR BREAKING CHANGES**
  - Entity state changes aren't reflected until the next system starts processing
    or a new `World#process` round begins, whichever comes first. This behavior
    can be configured during world creation so entity state changes are only
    propagated at start of `World#process` using the `WorldConfiguration` class.

- The GWT backend can now read values off annotations.
- **Bytecode optimizations:** (invoked via maven plugin or the CLI tool)
  - [Automatically optimize entity processing systems](https://github.com/junkdog/artemis-odb/issues/123).
  - `@PackedWeaver` components no longer overwrite data in multi-World scenarios.
- Removed `artemis-benchmark`, refer to [entity-system-benchmarks](https://github.com/junkdog/entity-system-benchmarks) instead.
- It's no longer necessary to call `Entity#changedInWorld` and `Entity#addToWorld`
  - Use `Entity#edit` when adding or removing components.
- Adding and removing entities to systems is now approximately 60% faster.
  - Entity compositions are no longer primarily identified by BitSets, but instead
    have a compositionId - EntitySystems track which composition ids are of interest.
  - Optimized `EntitySystem#check`, entities are processed in bulk, less checks when removing entities,
    removed systemIndex.
- [@Profile](https://github.com/junkdog/artemis-odb/wiki/@Profile) entity systems with custom classes.
- New WorldConfiguration class
  - Set expected entity count
  - Limit number of rebuilt active entities per system/tick. Rebuilt indices ensure entities are
    processed in sequential order.
  - Control how often entity state changes are propagated to systems and managers.
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

## Version: 0.6.5 - 2014-07-17
- Better support for multiple concurrent worlds in regards to memory usage.
- Smaller entity instances.
- **Fix**: Actually fixed GWT support, bumped gwt to 2.6.0.

## Version: 0.6.3 - 2014-07-07
- **Fix**: Potential IOOB exceptions in UuidEntityManager.

## Version: 0.6.2 - 2014-07-07
- UUID is now optional for entities.
  - Add UuidEntityManager to automatically map UUID:s to entities.
  - UUID:s are lazily instantiated.
- **Fix**: GWT build was broken in 0.6.1.

## Version: 0.6.1 - 2014-07-03
- New standalone `artemis-odb-cli` artifact:
  - Handles weaving (new in 0.6.1) and matrix generation.
  - Replaces `artemis-odb-matrix-X.Y.Z-cli.jar`.
- **Fix**: OOB Exception in ComponentManager with componentsByType when component types > 64.
- **Fix**: Calling `Entity#changedInWorld` before an entity is added to the world yields null.

## Version: 0.6.0 - 2014-06-01
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
- Entity systems and managers can `@Wire` (inject) anything from the world: will eventually
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
- **Fix**: `Entity#getComponent` would sometimes throw an `ArrayIndexOutOfBoundsException`.

## Version: 0.5.0 - 2013-11-24
 - Changed artemis to a multi-module project (the `artemis` folder is the old root).
 - Entity instances are recycled.
 - New component types, `PooledComponent` and `PackedComponent`.
   - Optionally transform components with `@PackedWeaver` and `@PooledWeaver` by
     configuring the `artemis-odb-maven-plugin`.
 - New method `Entity#createComponent(Class<Component>)`.
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
