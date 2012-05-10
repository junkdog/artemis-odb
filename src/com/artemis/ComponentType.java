package com.artemis;

public class ComponentType {
	private static long nextBit = 1;
	private static int nextId = 0;
	
	private long bit;
	private int id;
	
	public ComponentType() {
		init();
	}
	
	private void init() {
		bit = nextBit;
		nextBit = nextBit << 1;
		id = nextId++;
	}
	
	public long getBit() {
		return bit;
	}
	
	public int getId() {
		return id;
	}
}
