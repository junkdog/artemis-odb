# Change Log

## Version: HEAD
 - Changed artemis to a multi-module project (the `artemis` folder is the old root).
 - Entity instances are recycled.
 - New component types, `PooledComponent` and `PackedComponent`.
   - Optionally transform components with `@PackedWeaver` and `@PooledWeaver` by
     configuring the `artemis-odb-maven-plugin`.
 - Annotation processor validates pooled and packed component types.
 - Fix: DelayedEntityProcessingSystem prematurely expired entities.
