

## Functionality changes:
- Split World with CosplayWorld<T>
- Split EntityManager with CosplayEntityManager<T>
- Split BaseComponentMapper with CosplayComponentMapper<T>

## Deprecated
- removed EntityEdit#getEntity -> @todo merge with example EntityImpl
- removed EntityTransmuter#transmute -> @todo merge with example EntityImpl.
- EntityBuilder#build() returns id now -> @todo merge with example EntityImpl.

## TODO
- Port the whole fluid API to cosplay.
- Port LinkFactory
- Reintegrate EntityFieldMutator
- Reintegrate EntityBagFieldMutator
- Integrate dangling CosplayComponentMapper
- Pull back into core:
-- TagManager
-- GroupManager
-- EntityBuilder
- can we flatten EntitySystem, Manager and BaseEntitySystem? There seems to be some leaking regarding subscriptions.
- serializer still depends on odb-cosplay.

## Blocking points
- Need for a separate ComponentMapper class to expose Entity object specific methods is a bit of a step back.
-- add ComponentMapper generic to World!?

## To test
-- managers are now initialized alongside systems using Initialize, instead of special method.

## Cleanup
- World vs worldTyped in CosplayBaseSystem -> Generic on BaseSystem?
- public LinkFactory.ReflexiveMutators getReflextiveMutators() bit messy visibility of things.
- We have some duplicate tests!?

Perhaps best to store Entity on odb-core to simplify serialization.