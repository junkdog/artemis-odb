## Artemis-odb

[![Join the chat at https://gitter.im/junkdog/artemis-odb](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/junkdog/artemis-odb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/junkdog/artemis-odb.svg)](https://travis-ci.org/junkdog/artemis-odb)


[Artemis-odb](https://github.com/junkdog/artemis-odb/wiki/About) is a [high performance](https://github.com/junkdog/entity-system-benchmarks) java based [Entity-Component-System](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems) framework.

It is actively maintained, and a continuation of the popular [Artemis](http://gamadu.com/artemis/).

### Highlights
- Full GWT support.
- High performance, GC-friendly, [less boilerplate](https://github.com/junkdog/artemis-odb/wiki/@Wire)!
- [struct emulation](https://github.com/junkdog/artemis-odb/wiki/Packed-Weaver) and [pooled](https://github.com/junkdog/artemis-odb/wiki/@PooledWeaver) components, [hotspot optimization](https://github.com/junkdog/artemis-odb/wiki/Hotspot-Optimization), leveraged by [bytecode injection](https://github.com/junkdog/artemis-odb/wiki/Bytecode weaving).
- Convenient [dependency matrices](https://github.com/junkdog/artemis-odb/wiki/Component Dependency Matrix) for your components and systems.
- Backwards compatible with vanilla artemis, mostly. See CHANGELOG for minor breaking changes.

_For more details, see [CHANGELOG.md](https://github.com/junkdog/artemis-odb/blob/master/CHANGELOG.md)_

### Benchmarks
artemis-odb is one of the fastest incarnations of Artemis. 

Operations/second. Higher is better.

![it16k][it16k] ![ir16k][ir16k] ![arc16k][arc16k]

See [entity-system-benchmarks](https://github.com/junkdog/entity-system-benchmarks) for more benchmarks.
Don't take our word for it, run the benchmarks yourself!

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


### Made with artemis-odb

Browse games in the [Game Gallery](https://github.com/junkdog/artemis-odb/wiki/Game-Gallery)!

#### Commercial games

- [Dog Sled Saga](http://www.dogsledsaga.com/) ([Steam page](http://store.steampowered.com/app/286240/)), "desktop and mobile racing game with a saga of rank climbing, reputation building, team management, and pet loving".
  - [Polygon piece on Dog Sled Saga](http://www.polygon.com/2013/5/22/4344100/dog-sled-saga).
  
#### Open source games
| game                          | source/binaries                      | [matrix][cdm]     | author                         |
|-------------------------------|--------------------------------------|-------------------|--------------------------------|
| [Arktrail][ark]               | [source][ark-src] [play][ark-play]   | N/A               | [@DaanVanYperen][dvy]          |
| [Sine][sine]                  | [source][sine-src]                   | [cdm][sine-cdm]   | [@timtipgames][sine-auth]      |
| Spaceship Warrior Redux       | [source][sw-src] [play][sw-play]     | [cdm][sw-cdm]     | updated by [@Flet][flet]       |
| [Ned et les Maki][ned]        | [source][ned-src]                    | N/A               | [Geeky Gobling Prod][ned-auth] |
| [Naturally Selected 2D][ns2d] | [source][ns2d-src] [play][ns2d-play] | [cdm][ns2d-cdm]   | [@DaanVanYperen][dvy]          |
| [The Underkeep][tu]           | [source][tu-src] [play][tu-play]     | [cdm][tu-cdm]     | [@DaanVanYperen][dvy]          |
| [Tox][tox]                    | [source][tox-src] [play][tox-play]   | N/A               | [@DaanVanYperen][dvy]          |
| [Zombie Copter][zc-src]       | [source][zc-src] (ported from Ashley)                    | N/A               | [@Deftwun][dw]                 |

 [ark]: http://www.ludumdare.com/compo/ludum-dare-30/?action=preview&uid=22396
 [ark-src]: https://github.com/DaanVanYperen/arktrail
 [ark-play]: http://www.mostlyoriginal.net/play-arktrail/
 [cdm]: https://github.com/junkdog/artemis-odb/wiki/Component-Dependency-Matrix
 [cli]: https://github.com/junkdog/artemis-odb/wiki/Command-Line-Tool
 [sine]: http://www.ludumdare.com/compo/ludum-dare-27/?action=preview&uid=15341
 [sine-src]: https://dl.dropboxusercontent.com/u/3057562/sine/sine-src.jar
 [sine-cdm]: http://junkdog.github.io/matrix-sine.html
 [sine-auth]: http://twitter.com/timtipgames
 [ned]: http://devnewton.bci.im/en/games/nedetlesmaki
 [ned-src]: https://github.com/devnewton/nedetlesmaki
 [ned-auth]: http://geekygoblin.org/
 [ns2d]: http://www.ludumdare.com/compo/minild-50/?action=preview&uid=22396
 [ns2d-src]: https://github.com/DaanVanYperen/naturally-selected-2d
 [ns2d-play]: http://ludum.mostlyoriginal.net/game/ns2d/
 [ns2d-cdm]: http://junkdog.github.io/matrix-ns2d.html
 [tu]: http://www.ludumdare.com/compo/ludum-dare-29/?action=preview&uid=22396
 [tu-src]: https://github.com/DaanVanYperen/underkeep
 [tu-play]: http://www.mostlyoriginal.net/play-underkeep/
 [tu-cdm]: http://junkdog.github.io/matrix-tu.html
 [tox]: http://7drl.org/2014/03/17/tox-puzzle-roguelike-dont-do-drugs-kids/
 [tox-src]: https://github.com/DaanVanYperen/tox/
 [tox-play]: http://ludum.mostlyoriginal.net/game/tox/
 [dvy]: https://github.com/DaanVanYperen
 [sw-src]: https://github.com/Flet/spasceship-warrior-gradle
 [sw-play]: http://flet.github.io/spaceship-warrior-redux/
 [sw-cdm]: http://junkdog.github.io/matrix-sw.html
 [flet]: https://github.com/Flet/ 
 [zc]: https://github.com/Deftwun/ZombieCopter
 [zc-src]: https://github.com/DaanVanYperen/artemis-odb-zombie-copter
 [dw]: https://github.com/Deftwun

### Dependency Matrix

Generate dependency matrix of your existing systems, managers and components.

[![Shaman's Weirding Game: Component Dependency Matrix](https://raw.githubusercontent.com/wiki/junkdog/artemis-odb/images/cdm.png)](http://junkdog.github.io/matrix.html)

### Getting started

#### Community

Share your thoughts and questions with us!

- **[Gitter web chat](https://gitter.im/junkdog/artemis-odb)**
- **[issues](https://github.com/junkdog/artemis-odb/issues)**
- [Google groups](https://groups.google.com/forum/#!forum/artemis-odb)
- [Slick forums](http://slick.ninjacave.com/forum/viewforum.php?f=28)

#### Maven
```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>0.11.4</version>
</dependency>
```

See [weave automation](https://github.com/junkdog/artemis-odb/wiki/Weave-Automation) and [module overview](https://github.com/junkdog/artemis-odb/wiki/Module-Overview)

#### Gradle
```groovy
  dependencies { compile "net.onedaybeard.artemis:artemis-odb:0.11.4" }
```

#### Manual Download

 - [Main library](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb/0.11.4/) 
 - [Command-line tool](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb-cli/0.11.4/)
 - [Entity Factory Processor](http://repo1.maven.org/maven2/net/onedaybeard/artemis/artemis-odb-processor/0.11.4/)

