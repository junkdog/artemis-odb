# Change Log

## Version: 0.6.6-SNAPSHOT
- The GWT backend can now read values off annotations.
- Removed `artemis-benchmark`, refer to [entity-system-benchmarks](https://github.com/junkdog/entity-system-benchmarks) instead.
- [@Profile](https://github.com/junkdog/artemis-odb/wiki/@Profile) entity systems with custom classes.
- New interface `PackedComponent.DisposedWithWorld` for freeing packed components' resources when
  disposing the world.
  - Automatically added to all `@PackedWeaver` components.
- `World#inject(Object)` can inject normal java classes with artemis types.
  - Requires that the injected object is annotated with `@Wire`
- Added `World#getManagers` method.
- **Fix**: Disabled entities are no longer added to systems.


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
