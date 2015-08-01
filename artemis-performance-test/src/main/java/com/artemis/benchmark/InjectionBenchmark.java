package com.artemis.benchmark;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.benchmark.domain.Domain.ComplexSystem;
import com.artemis.benchmark.domain.Domain.CustomWired;
import com.artemis.benchmark.domain.Domain.DamageSystem;
import com.artemis.benchmark.domain.Domain.PositionSystem;
import com.artemis.benchmark.domain.Domain.VelocitySystem;
import com.artemis.injection.CachedInjector;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
public class InjectionBenchmark {

    WorldConfiguration configuration;

    @Setup(Level.Iteration)
    public void restart() {
        configuration = new WorldConfiguration();
        configuration.setManager(new TagManager());
        configuration.setManager(new UuidEntityManager());
        configuration.setSystem(new PositionSystem());
        configuration.setSystem(new VelocitySystem());
        configuration.setSystem(new DamageSystem());
        configuration.setSystem(new ComplexSystem());
        configuration.register("string", "STRING");
        configuration.register(new Object());
    }

    @Benchmark
    public void createWorld_with_injectionCache() {
        configuration.setInjector(new CachedInjector());
        createWorld();
    }

    private void createWorld() {
        World world = new World(configuration);
        world.inject(new CustomWired());
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InjectionBenchmark.class.getSimpleName())
                .shouldDoGC(true)
                .resultFormat(ResultFormatType.JSON)
                .result("benchmark" + System.currentTimeMillis() + ".json")
                .addProfiler(StackProfiler.class)
                .jvmArgsAppend("-Djmh.stack.period=1")
                .build();

        new Runner(opt).run();
    }


}
