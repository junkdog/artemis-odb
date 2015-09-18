package com.artemis.weaver.packed;

import java.io.Serializable;
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
	private List<AbstractInsnNode> deletions;
	private int originalIndex;

	private InstructionMutator(InsnList instructions, AbstractInsnNode reference) {
		this.instructions = instructions;
		this.reference = reference;
		
		insertions = new TreeMap<Integer, AbstractInsnNode[]>();
		deletions = new ArrayList<AbstractInsnNode>();
		originalIndex = instructions.indexOf(reference);
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
		deletions.add(instructions.get(originalIndex - offset));
		indexChange--;
		return this;
	}
	
	public int transform() {
		ArrayList<Integer> offsets = new ArrayList<Integer>(insertions.keySet());
		Collections.sort(offsets, new ReversedComparator());
		for (int offset : offsets) {
			int refIndex = instructions.indexOf(reference);
			AbstractInsnNode ref = instructions.get(refIndex - offset);
			for (AbstractInsnNode n :  insertions.get(offset)) {
				instructions.insertBefore(ref, n);
			}
		}
		
		for (AbstractInsnNode node : deletions) {
			instructions.remove(node);
		}
		
		return originalIndex + indexChange;
	}
	
	private static final class ReversedComparator implements Comparator<Integer>, Serializable {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2.compareTo(o1);
		}
	}
}