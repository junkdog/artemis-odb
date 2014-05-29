/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.artemis;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.artemis.component.PlainPosition;
import com.artemis.component.PlainStructComponentA;
import com.artemis.component.Position;
import com.artemis.component.StructComponentA;
import com.artemis.system.EntityDeleterSystem;
import com.artemis.system.PlainPositionSystem;
import com.artemis.system.PositionSystem;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 20, timeUnit = TimeUnit.SECONDS)
public class PackedBenchmark {
	
	public static final int ENTITY_COUNT = 4096;
	
	public static long seed = System.currentTimeMillis();
	
	private World worldPacked;
	private World worldPlain;

	private World worldBaseline;
	
	@Setup
	public void init() {
		worldPacked = new World();
		worldPacked.setSystem(new PositionSystem());
		worldPacked.setSystem(new EntityDeleterSystem(seed) {
			@Override
			protected void createEntity() {
				Entity e = world.createEntity();
				e.createComponent(Position.class);
				e.createComponent(StructComponentA.class);
				e.addToWorld();
			}
			
		});
		worldPacked.initialize();
		
		worldPlain = new World();
		worldPlain.setSystem(new PlainPositionSystem());
		worldPlain.setSystem(new EntityDeleterSystem(seed) {
			@Override
			protected void createEntity() {
				Entity e = world.createEntity();
				e.createComponent(PlainPosition.class);
				e.createComponent(PlainStructComponentA.class);
				e.addToWorld();
			}
		});
		worldPlain.initialize();
		
		worldBaseline = new World();
		worldBaseline.setSystem(new EntityDeleterSystem(seed) {
			@Override
			protected void createEntity() {
				Entity e = world.createEntity();
				e.addToWorld();
			}
		});
		worldBaseline.initialize();
		
		for (int i = 0; ENTITY_COUNT > i; i++) {
			createEntity(worldPacked, Position.class, PlainStructComponentA.class);
			createEntity(worldPlain, PlainPosition.class, PlainStructComponentA.class);
		}
	}
	
	@GenerateMicroBenchmark
	public void packed_position_world() {
		worldPacked.process();
	}
	
	
	@GenerateMicroBenchmark
	public void plain_position_world() {
		worldPlain.process();
	}
	
	@GenerateMicroBenchmark
	public void baseline_world() {
		worldPlain.process();
	}
	
	public static void createEntity(World world, Class<? extends Component> c1, Class<? extends Component> c2) {
		Entity e = world.createEntity();
		e.createComponent(c1);
		e.createComponent(c2);
		e.addToWorld();
	}
}
