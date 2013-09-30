package com.artemis;

import com.artemis.utils.Bag;
import java.util.Iterator;

/**
 * ArtemisBenchmark.
 * Some benchmarks for profiling Artemis.
 * @author lopho
 */
public class ArtemisBenchmark {
	public static void main(String[] args) {
		start();
	}

    /**
     * Amount of entities to create for benchmarks (except bag test).
     */
    private static final int amount = 10;
    /**
     * Iterations before profiling results will reset, for warming up JIT.
     */
    private static final long warmUp = 10000;
    /**
     * Iterations the profiler should collect data.
     */
    private static final long duration = 100000;

    private static long current = 0;
    private static boolean done = false;
    private static boolean reset = false;
    private static final World world = new World();
    private static Entity dummy = null;
    private static final Entity[] e = new Entity[amount];
    private static final Bag<Entity> staticBag = new Bag<Entity>();
    static {
        for (int i = 0; i < amount; i++) {
            e[i] = world.createEntity();
            e[i].addToWorld();
        }

        for (int i = 0; i < amount; i++) {
            staticBag.add(world.createEntity());
        }

        world.process();
    }

    /**
     * World#check() benchmark.
     */
    private static void runWorld() {
        for (int i = 0, s = e.length; i < s; i++) {
            // make sure the performers have something to do.
            e[i].changedInWorld();
        }
        world.initialize();
        world.process();
    }

    /**
     * Bag#clear() benchmark.
     * Change 64 to whatever if you want to check out the grow methods.
     */
    private static void runBag() {
        Bag<Entity> bag = new Bag<Entity>();
        for (int i = 0; i < 64; i++) {
            bag.add(e[i]);
        }
        bag.clear();
    }

    /**
     * BagIterator#next() benchmark.
     * @param bag
     */
    private static void runBagIterator() {
        Bag<Entity> bag = createBag();
        Iterator<Entity> it = bag.iterator();
        for (int i = 0; i < amount; i++) {
            dummy = it.next();
        }
    }

    /**
     * BagIterator#next() benchmark using staticBag for all iterations.
     */
    private static void runBagIteratorStaticBag() {
        Iterator<Entity> it = staticBag.iterator();
        for (int i = 0; i < amount; i++) {
            dummy = it.next();
        }
    }

    /**
     * Creates a bag for the Iterator benchmark.
     * @return
     */
    private static Bag<Entity> createBag() {
        Bag<Entity> bag = new Bag<Entity>();
        for (int i = 0; i < amount; i++) {
            bag.add(e[i]);
        }
        return bag;
    }

    /**
     * Comment out all benchmarks but the one you want to run.
     */
    public static void start() {
        // warmup for the jit
        while(!done) {
            //runBag();
            //runBagIterator(createBag());
            //runBagIteratorStaticBag();
            runWorld();
            current++;
            if (current == warmUp) {
                done = true;
            }
        }

        current = 0;
        done = false;

        // create profiling point that resets data collected so far here
        reset = true;

        // here the data should be collected
        while(!done) {
            //runBag();
            //runBagIterator(createBag());
            //runBagIteratorStaticBag();
            runWorld();
            current++;
            if (current == duration) {
                done = true;
            }
        }
    }

}
