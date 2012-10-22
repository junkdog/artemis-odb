# Preamble

This is a fork of [Artemis Entity System Framework](http://gamadu.com/artemis/).

# What's changed

 - More efficient for loops, bounds checking performed once during loop initialization.
 - No more classes instantiated every time [World.process](https://github.com/junkdog/artemis-odb/blob/ed9e9b4bc29362b3f95beb60f9160a433ddc045a/src/com/artemis/World.java#L325) is run.
 - Systems can be enabled/disabled, added <code>EntitySystem.setEnabled(boolean)</code> and <code>EntitySystem.isEnabled()</code>.