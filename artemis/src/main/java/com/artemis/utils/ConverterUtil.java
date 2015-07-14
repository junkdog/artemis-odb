package com.artemis.utils;

import java.util.BitSet;

public final class ConverterUtil {
	private ConverterUtil() {}

	public static IntBag toIntBag(BitSet bs, IntBag out) {
		int size = bs.cardinality();
		out.setSize(size);
		out.ensureCapacity(size);

		int[] activesArray = out.getData();
		for (int i = bs.nextSetBit(0), index = 0; i >= 0; i = bs.nextSetBit(i + 1)) {
			activesArray[index++] = i;
		}

		return out;
	}

	public static BitSet toBitSet(IntBag bag, BitSet out) {
		int[] data = bag.getData();
		for (int i = 0, s = bag.size(); s > i; i++) {
			out.set(data[i]);
		}

		return out;
	}
}