package com.artemis.weaver.packed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

class InstructionMutator {

	private final InsnList instructions;
	private final AbstractInsnNode reference;
	private int indexChange;
	
	private TreeMap<Integer,AbstractInsnNode[]> insertions;
	private List<Integer> deletions;

	private InstructionMutator(InsnList instructions, AbstractInsnNode reference) {
		this.instructions = instructions;
		this.reference = reference;
		
		insertions = new TreeMap<Integer, AbstractInsnNode[]>();
		deletions = new ArrayList<Integer>();
	}
	
	public static InstructionMutator on(InsnList instructions, AbstractInsnNode reference) {
		return new InstructionMutator(instructions, reference);
	}
	
	public InstructionMutator insertAtOffset(int offset, AbstractInsnNode... nodes) {
		if (nodes == null)
			return this;

		insertions.put(offset, nodes);
		indexChange += nodes.length;
		return this;
	}

	public InstructionMutator delete(int offset) {
		deletions.add(offset);
		indexChange--;
		return this;
	}
	
	public int transform() {
		int originalIndex = instructions.indexOf(reference);
		
		ArrayList<Integer> offsets = new ArrayList<Integer>(insertions.keySet());
		Collections.sort(offsets, new ReversedComparator());
		for (int offset : offsets) {
			System.out.println("\t +opcodes: " + insertions.get(offset).length);
			int refIndex = instructions.indexOf(reference);
			AbstractInsnNode ref = instructions.get(refIndex - offset);
			for (AbstractInsnNode n :  insertions.get(offset)) {
				instructions.insertBefore(ref, n);
			}
		}
		
		Collections.sort(deletions, new ReversedComparator());
		System.out.println("\t -opcodes: " + deletions.size());
		for (int offset : deletions) {
			int refIndex = instructions.indexOf(reference);
			AbstractInsnNode ref = instructions.get(refIndex - offset);
			instructions.remove(ref);
		}
		
		return originalIndex + indexChange;
	}
	
	private static final class ReversedComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2.compareTo(o1);
		}
	}
}