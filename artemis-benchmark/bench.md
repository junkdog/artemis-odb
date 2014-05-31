## Performance

*Take the results with a grain of salt. There are no guarantees that the benchmark is correct,
but hopefully.*

All benchmarks run `World#process` - at every 100th iteration an entity is deleted and then
recreated. The *packed* and *plain* benchmarks have an additional system which updates a 
single component for all entities at every iteration; *plain* and *packed* work with normal
`com.artemis.Component' and 'com.artemis.PackedComponent' respectively.

```
Benchmark                                     Mode   Samples         Mean   Mean error    Units
c.a.PackedBenchmark.baseline_world            avgt        50       40.219        0.046    us/op
c.a.PackedBenchmark.packed_position_world     avgt        50       40.279        0.089    us/op
c.a.PackedBenchmark.plain_position_world      avgt        50       40.705        0.082    us/op
```

#### Running the benchmarks

From artemis' root folder:

```
mvn clean install
java -jar artemis-benchmark/target/microbenchmarks.jar -t 1 -f 5
```

To get less noisy results, consider:

- Rebooting the computer
- Stop any non-vital background processes
- Disable all network adapters.

