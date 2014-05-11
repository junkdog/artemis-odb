## Preamble

This is a [fork](https://code.google.com/p/artemis-framework/) of [Artemis Entity System Framework](http://gamadu.com/artemis/).

## Discussion/Forum

There's a google group at https://groups.google.com/forum/#!forum/artemis-odb - in addition to the issues, where hitherto much of the discussion has taken place. The original artemis forum is still around too, though it doesn't see much traffic, nor is it specific to artemis-odb: http://slick.javaunlimited.net/viewforum.php?f=28

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

#### Other
 - **artemis-weaver:** Transforms `com.artemis.Component` classes when annotated with `@PooledWeaver` or `@PackedWeaver`, see _artemis-maven_.
 - **artemis-test:** Compliance tests.

## Maven
### Embracing Artemis
```xml
	<properties>
		<artemis.version>0.5.0</artemis.version>
	</properties>
	
	<dependencies>
		<!-- base library -->
		<dependency>
			<groupId>net.onedaybeard.artemis</groupId>
			<artifactId>artemis-odb</artifactId>
			<version>${artemis.version}</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>net.onedaybeard.artemis</groupId>
				<artifactId>artemis-odb-maven-plugin</artifactId>
				<version>${artemis.version}</version>
				<executions>
					<execution>
						<goals>
							<!-- enables @PooledWeaver and @PackedWeaver -->
							<goal>artemis</goal>
							
							<!-- generates the component dependency matrix report -->
							<goal>matrix</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

### Minimal (no weaving/compile-time transformations)
```xml
<dependency>
	<groupId>net.onedaybeard.artemis</groupId>
	<artifactId>artemis-odb</artifactId>
	<version>0.5.0</version>
</dependency>
```



## Agrotera: Anti-boilerplate lib for Artemis
[Agrotera](http://github.com/junkdog/agrotera) uses annotations to configure EntitySystems, removing the
need for defining Aspects inside constructors and takes care of injecting ComponentMappers,
Managers and EntitySystems. Can also simulate aspects for Managers and inject profiling calls.

## Games using this library
### Open source
| game                          | source/binaries                      | [matrix][cdm]     | author                         |
|-------------------------------|--------------------------------------|-------------------|--------------------------------|
| [Sine][sine]                  | [source][sine-src]                   | [cdm][sine-cdm]   | [@timtipgames][sine-auth]      |
| [Ned et les Maki][ned]        | [source][ned-src]                    | N/A               | [Geeky Gobling Prod][ned-auth] |
| [Naturally Selected 2D][ns2d] | [source][ns2d-src] [play][ns2d-play] | [cdm][ns2d-cdm]   | [@DaanVanYperen][dvy]          |
| [The Underkeep][tu]           | [source][tu-src] [play][tu-play]     | [cdm][tu-cdm]     | [@DaanVanYperen][dvy]          |

 [cdm]: https://github.com/junkdog/artemis-odb/wiki/Component-Dependency-Matrix
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
 [dvy]: https://github.com/DaanVanYperen
 
