## Artemis-odb

[![Join the chat at https://gitter.im/junkdog/artemis-odb](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/junkdog/artemis-odb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/junkdog/artemis-odb.svg)](https://travis-ci.org/junkdog/artemis-odb)


[Artemis-odb](https://github.com/junkdog/artemis-odb/wiki/About) is a [high performance](https://github.com/junkdog/entity-system-benchmarks) java based [Entity-Component-System](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems) framework.

It is mature, actively maintained, and a continuation of the popular [Artemis](http://gamadu.com/artemis/).


### Highlights

- Full Android, [HTML5](https://github.com/junkdog/artemis-odb/wiki/GWT), iOS support.
- High performance, GC-friendly, [less boilerplate](https://github.com/junkdog/artemis-odb/wiki/@Wire)!
- Optionally [auto-pooled](https://github.com/junkdog/artemis-odb/wiki/%40PooledWeaver) components and [hotspot optimization](https://github.com/junkdog/artemis-odb/wiki/Hotspot-Optimization) via compile-time [bytecode instrumentation](https://github.com/junkdog/artemis-odb/wiki/Bytecode-weaving).
- Serialize to either [json](https://github.com/junkdog/artemis-odb/wiki/libgdx-json) or [binary](https://github.com/junkdog/artemis-odb/wiki/Kryo-Serialization).
- Automatic tracking, and maintenance of [entity relationships](https://github.com/junkdog/artemis-odb/wiki/EntityLinkManager).
- Fast prototyping with [fluid interfaces](https://github.com/junkdog/artemis-odb/wiki/Fluid-Entities) (optional).
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

<img src="https://static.jam.vg/raw/64d/z/1275f.gif" width="450">

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

#### Maven

```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>2.2.0-SNAPSHOT</version>
</dependency>
```

See [weave automation](https://github.com/junkdog/artemis-odb/wiki/Weave-Automation) and [module overview](https://github.com/junkdog/artemis-odb/wiki/Module-Overview)

#### Gradle

```groovy
  dependencies { compile "net.onedaybeard.artemis:artemis-odb:2.2.0-SNAPSHOT" }
```

#### Manual Download

 - [Main library](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb/) 
 - [Command-line tool](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb-cli/)

