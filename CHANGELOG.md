# Change Log

## In development
 - Direct field access works for `@PackedWeaver` components.

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

