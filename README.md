## Artemis-odb

[![Join the chat at https://gitter.im/junkdog/artemis-odb](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/junkdog/artemis-odb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/junkdog/artemis-odb.svg)](https://travis-ci.org/junkdog/artemis-odb)


[Artemis-odb](https://github.com/junkdog/artemis-odb/wiki/About) is a [high performance](https://github.com/junkdog/entity-system-benchmarks) java based [Entity-Component-System](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems) framework.

It is mature, actively maintained, and a continuation of the popular [Artemis](http://gamadu.com/artemis/).

### Highlights

- Full Android, [HTML5](https://github.com/junkdog/artemis-odb/wiki/GWT), iOS support.
- High performance, GC-friendly, [less boilerplate](https://github.com/junkdog/artemis-odb/wiki/@Wire)!
- Compile-time [bytecode instrumentation](https://github.com/junkdog/artemis-odb/wiki/Bytecode weaving) offers opt-in [pooled](https://github.com/junkdog/artemis-odb/wiki/@PooledWeaver) components, and [hotspot optimization](https://github.com/junkdog/artemis-odb/wiki/Hotspot-Optimization).
- Serialize to either [json](https://github.com/junkdog/artemis-odb/wiki/Json Serialization) or [binary](https://github.com/junkdog/artemis-odb/wiki/Kryo-Serialization).
- Convenient [dependency matrices](https://github.com/junkdog/artemis-odb/wiki/Component Dependency Matrix) for your components and systems.
- Easy migration from Artemis clones.

_See [CHANGELOG.md](https://github.com/junkdog/artemis-odb/blob/master/CHANGELOG.md) for more details_

### Fast!

Artemis-odb is one of the fastest incarnations of Artemis! 
Don't take our word for it, check out and [run the benchmarks](https://github.com/junkdog/entity-system-benchmarks) yourself!

Operations/second. Higher is better.

![it16k][it16k] ![ir16k][ir16k] ![arc16k][arc16k]

 [it1k]: http://junkdog.github.io/images/ecs-bench/iteration__1024_entities.png
 [it4k]: http://junkdog.github.io/images/ecs-bench/iteration__4096_entities.png
 [it16k]: http://junkdog.github.io/images/ecs-bench/iteration__16384_entities.png
 [it65k]: http://junkdog.github.io/images/ecs-bench/iteration__65536_entities.png
 [ir1k]: http://junkdog.github.io/images/ecs-bench/insert_remove__1024_entities.png
 [ir4k]: http://junkdog.github.io/images/ecs-bench/insert_remove__4096_entities.png
 [ir16k]: http://junkdog.github.io/images/ecs-bench/insert_remove__16384_entities.png
 [ir65k]: http://junkdog.github.io/images/ecs-bench/insert_remove__65536_entities.png 
 [arc1k]: http://junkdog.github.io/images/ecs-bench/add_remove_components__1024_entities.png
 [arc4k]: http://junkdog.github.io/images/ecs-bench/add_remove_components__4096_entities.png
 [arc16k]: http://junkdog.github.io/images/ecs-bench/add_remove_components__16384_entities.png
 [arc64k]: http://junkdog.github.io/images/ecs-bench/add_remove_components__65536_entities.png 

### Learn from others!

Dozens of games with source available in the [Game Gallery](https://github.com/junkdog/artemis-odb/wiki/Game-Gallery)!

<img src="http://i.imgur.com/DHy5k6h.png" width="235">
<img src="http://tikotepadventure.com/files/tikotep/201507/monolith.gif" width="235">
<img src="http://ludumdare.com/compo/wp-content/compo2//375043/22396-shot2.png-eq-900-500.jpg" width="235">

### Use it commercially!

[Dog Sled Saga](http://www.dogsledsaga.com/)

### Expand your toolkit!

 [Tools, Extensions and Frameworks](https://github.com/junkdog/artemis-odb/wiki/Extensions)

[<img src="https://raw.githubusercontent.com/wiki/junkdog/artemis-odb/images/cdm.png" width="350">](http://junkdog.github.io/matrix.html)
[<img src="https://github.com/Namek/artemis-odb-entity-tracker/raw/master/screenshot.png" width="350">](https://github.com/Namek/artemis-odb-entity-tracker)

### Getting started

#### Community

Share your thoughts and questions with us!

- **[Gitter web chat](https://gitter.im/junkdog/artemis-odb)**
- **[issues](https://github.com/junkdog/artemis-odb/issues)**
- [Google groups](https://groups.google.com/forum/#!forum/artemis-odb)
- [Slick forums](http://slick.ninjacave.com/forum/viewforum.php?f=28)

#### Maven


Anticipating 2.0.0: minor API changes may still occur

```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>2.0.0-RC1</version>
</dependency>
```

Or, the safe route:

```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>1.4.0</version>
</dependency>
```

See [weave automation](https://github.com/junkdog/artemis-odb/wiki/Weave-Automation) and [module overview](https://github.com/junkdog/artemis-odb/wiki/Module-Overview)

#### Gradle

```groovy
  dependencies { compile "net.onedaybeard.artemis:artemis-odb:2.0.0-RC1" }
```
or

```groovy
  dependencies { compile "net.onedaybeard.artemis:artemis-odb:1.4.0" }
```

#### Manual Download

 - [Main library](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb/2.0.0-RC1/) 
 - [Command-line tool](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb-cli/2.0.0-RC1/)

