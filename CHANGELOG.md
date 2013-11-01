# Change Log

## Version: HEAD
 - Changed artemis to a multi-module project (the `artemis` folder is the old root).
 - Entity instances are recycled.
 - New component types, `PooledComponent` and `PackedComponent`.
 - Annotation processor validates pooled and packed component types.
 - Fix: DelayedEntityProcessingSystem prematurely expired entities.

