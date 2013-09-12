# Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/).

# What's changed

 - More efficient for loops, bounds checking performed once during loop initialization.
 - _processEntities_ forgoes Bag's getter in favor of direct array access.
 - No more classes instantiated every time [World.process](https://github.com/junkdog/artemis-odb/blob/ed9e9b4bc29362b3f95beb60f9160a433ddc045a/src/com/artemis/World.java#L325) is run.
 - Systems can be enabled/disabled, see <code>EntitySystem.setEnabled(boolean)</code> and <code>EntitySystem.isEnabled()</code>.
 - Bag now sports <code>sort</code>; borrows [libGDX](http://libgdx.badlogicgames.com/)'s TimSort implementation.
 - Foreach-friendly Bag: implements a one instance per Bag iterator. (version: 0.3.5-SNAPSHOT)

# Maven

```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>0.3.4</version>
</dependency>
```


# Agrotera: Anti-boilerplate lib for Artemis
[Agrotera](http://github.com/junkdog/agrotera) uses annotations to configure EntitySystems, removing the
need for defining Aspects inside constructors and takes care of injecting ComponentMappers,
Managers and EntitySystems. Can also simulate aspects for Managers and inject profiling calls.
