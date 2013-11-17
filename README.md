## Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/).

## What's changed

_Somewhat outdated, see [CHANGELOG.md](https://github.com/junkdog/artemis-odb/blob/master/CHANGELOG.md) for
recent changes._

 - More efficient for loops, bounds checking performed once during loop initialization.
 - _processEntities_ forgoes Bag's getter in favor of direct array access.
 - No more classes instantiated every time [World.process](https://github.com/junkdog/artemis-odb/blob/ed9e9b4bc29362b3f95beb60f9160a433ddc045a/src/com/artemis/World.java#L325) is run.
 - Systems can be enabled/disabled, see <code>EntitySystem.setEnabled(boolean)</code> and <code>EntitySystem.isEnabled()</code>.
 - Bag now sports <code>sort</code>; borrows [libGDX](http://libgdx.badlogicgames.com/)'s TimSort implementation.
 - Foreach-friendly Bag: implements a one instance per Bag iterator.



### Module overview
#### Required
 - **artemis:** Base library.

#### Optional
 - **artemis-maven:** Maven plugin for transforming `@PooledWeaver` and `@PackedWeaver` components.
 - **artemis-validator:** Annotation processor, performs rudimentary checks on components.

#### Other
 - **artemis-weaver:** Transforms `com.artemis.Component` classes when annotated with `@PooledWeaver` or `@PackedWeaver`, see _artemis-maven_.
 - **artemis-test:** Compliance tests.

## Maven

```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>0.4.0</version>
</dependency>
```



## Agrotera: Anti-boilerplate lib for Artemis
[Agrotera](http://github.com/junkdog/agrotera) uses annotations to configure EntitySystems, removing the
need for defining Aspects inside constructors and takes care of injecting ComponentMappers,
Managers and EntitySystems. Can also simulate aspects for Managers and inject profiling calls.

## Games using this library
### Open source
- [Sine](http://www.ludumdare.com/compo/2013/08/27/sine-post-mortem/) by [@timtipgames](http://twitter.com/timtipgames)
