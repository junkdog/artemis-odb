/*******************************************************************************
 * Copyright 2011 See AUTHORS.libgdx file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.artemis.utils;

import com.artemis.ComponentManager;
import com.artemis.World;

import java.util.Arrays;

/**
 * <p>Performance optimized bitset implementation. Certain operations are
 * prefixed with <code>unsafe</code>; these methods perform no validation,
 * and are primarily leveraged internally to optimize access on entityId bitsets.</p>
 *
 * <p>Originally adapted from <code>com.badlogic.gdx.utils.Bits</code>, it has been
 * renamed to avoid namespace confusion.</p>
 *
 * @author mzechner
 * @author jshapcott
 * @author junkdog (fork/changes)
 *
 * @see com.artemis.EntityManager#registerEntityStore(BitVector)
 */
public class BitVector {

	long[] words = {0};

	public BitVector() {
	}

	/** Creates a bit set whose initial size is large enough to explicitly represent bits with indices in the range 0 through
	 * nbits-1.
	 * @param nbits the initial size of the bit set */
	public BitVector(int nbits) {
		checkCapacity(nbits >>> 6);
	}

	/** Creates a bit set based off another bit vector.
	 * @param copyFrom  */
	public BitVector(BitVector copyFrom) {
		words = Arrays.copyOf(copyFrom.words, copyFrom.words.length);
	}

	/** @param index the index of the bit
	 * @return whether the bit is set
	 * @throws ArrayIndexOutOfBoundsException if index < 0 */
	public boolean get(int index) {
		final int word = index >>> 6;
		return word < words.length &&
			(words[word] & (1L << index)) != 0L;
	}

	/** @param index the index of the bit to set
	 * @throws ArrayIndexOutOfBoundsException if index < 0 */
	public void set(int index) {
		final int word = index >>> 6;
		checkCapacity(word);
		words[word] |= 1L << index;
	}

	/** @param index the index of the bit to set
	 * @throws ArrayIndexOutOfBoundsException if index < 0 */
	public void set(int index, boolean value) {
		if (value) {
			set(index);
		} else {
			clear(index);
		}
	}

	/** @param index the index of the bit
	 * @return whether the bit is set
	 * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length</></>*/
	public boolean unsafeGet(int index) {
		return (words[index >>> 6] & (1L << index)) != 0L;
	}

	/** @param index the index of the bit to set
	 * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length */
	public void unsafeSet(int index) {
		words[index >>> 6] |= 1L << index;
	}

	/** @param index the index of the bit to set
	 * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length */
	public void unsafeSet(int index, boolean value) {
		if (value) {
			unsafeSet(index);
		} else {
			unsafeClear(index);
		}
	}

	/** @param index the index of the bit to flip */
	public void flip(int index) {
		final int word = index >>> 6;
		checkCapacity(word);
		words[word] ^= 1L << index;
	}

	/**
	 * Grows the backing array (<code>long[]</code>) so that it can hold the requested
	 * bits. Mostly applicable when relying on the <code>unsafe</code> methods,
	 * including {@link #unsafeGet(int)} and {@link #unsafeClear(int)}.
	 *
	 * @param bits number of bits to accomodate
	 */
	public void ensureCapacity(int bits) {
		checkCapacity(bits >>> 6);
	}

	private void checkCapacity(int len) {
		if (len >= words.length) {
			long[] newBits = new long[len + 1];
			System.arraycopy(words, 0, newBits, 0, words.length);
			words = newBits;
		}
	}

	/** @param index the index of the bit to clear
	 * @throws ArrayIndexOutOfBoundsException if index < 0 */
	public void clear(int index) {
		final int word = index >>> 6;
		if (word >= words.length) return;
		words[word] &= ~(1L << index);
	}

	/** @param index the index of the bit to clear
	 * @throws ArrayIndexOutOfBoundsException if index < 0 or index >= words.length */
	public void unsafeClear(int index) {
		words[index >>> 6] &= ~(1L << index);
	}

	/** Clears the entire bitset */
	public void clear() {
		Arrays.fill(words, 0L);
	}

	/** Returns the "logical size" of this bitset: the index of the highest set bit in the bitset plus one. Returns zero if the
	 * bitset contains no set bits.
	 *
	 * @return the logical size of this bitset */
	public int length() {
		long[] bits = this.words;
		for (int word = bits.length - 1; word >= 0; --word) {
			long bitsAtWord = bits[word];
			if (bitsAtWord != 0)
				return (word << 6) + 64 - Long.numberOfLeadingZeros(bitsAtWord);
		}

		return 0;
	}

	/** @return true if this bitset contains no bits that are set to true */
	public boolean isEmpty() {
		long[] bits = this.words;
		int length = bits.length;
		for (int i = 0; i < length; i++) {
			if (bits[i] != 0L) {
				return false;
			}
		}
		return true;
	}

	/** Returns the index of the first bit that is set to true that occurs on or after the specified starting index. If no such bit
	 * exists then -1 is returned. */
	public int nextSetBit(int fromIndex) {
		int word = fromIndex >>> 6;
		if (word >= words.length)
			return -1;

		long bitmap = words[word] >>> fromIndex;
		if (bitmap != 0)
			return fromIndex + Long.numberOfTrailingZeros(bitmap);

		for (int i = 1 + word; i < words.length; i++) {
			bitmap = words[i];
			if (bitmap != 0) {
				return i * 64 + Long.numberOfTrailingZeros(bitmap);
			}
		}

		return -1;
	}

	/** Returns the index of the first bit that is set to false that occurs on or after the specified starting index. */
	public int nextClearBit(int fromIndex) {
		int word = fromIndex >>> 6;
		if (word >= words.length)
			return Math.min(fromIndex, words.length << 6);

		long bitmap = ~(words[word] >>> fromIndex);
		if (bitmap != 0)
			return fromIndex + Long.numberOfTrailingZeros(bitmap);

		for (int i = 1 + word; i < words.length; i++) {
			bitmap = ~words[i];
			if (bitmap != 0) {
				return i * 64 + Long.numberOfTrailingZeros(bitmap);
			}
		}

		return Math.min(fromIndex, words.length << 6);
	}

	/** Performs a logical <b>AND</b> of this target bit set with the argument bit set. This bit set is modified so that each bit in
	 * it has the value true if and only if it both initially had the value true and the corresponding bit in the bit set argument
	 * also had the value true.
	 * @param other a bit set */
	public void and(BitVector other) {
		int commonWords = Math.min(words.length, other.words.length);
		for (int i = 0; commonWords > i; i++) {
			words[i] &= other.words[i];
		}

		if (words.length > commonWords) {
			for (int i = commonWords, s = words.length; s > i; i++) {
				words[i] = 0L;
			}
		}
	}

	/** Clears all of the bits in this bit set whose corresponding bit is set in the specified bit set.
	 *
	 * @param other a bit set */
	public void andNot(BitVector other) {
		int commonWords = Math.min(words.length, other.words.length);
		for (int i = 0; commonWords > i; i++) {
			words[i] &= ~other.words[i];
		}
	}

	/** Performs a logical <b>OR</b> of this bit set with the bit set argument. This bit set is modified so that a bit in it has the
	 * value true if and only if it either already had the value true or the corresponding bit in the bit set argument has the
	 * value true.
	 * @param other a bit set */
	public void or(BitVector other) {
		int commonWords = Math.min(words.length, other.words.length);
		for (int i = 0; commonWords > i; i++) {
			words[i] |= other.words[i];
		}

		if (commonWords < other.words.length) {
			checkCapacity(other.words.length);
			for (int i = commonWords, s = other.words.length; s > i; i++) {
				words[i] = other.words[i];
			}
		}
	}

	/** Performs a logical <b>XOR</b> of this bit set with the bit set argument. This bit set is modified so that a bit in it has
	 * the value true if and only if one of the following statements holds:
	 * <ul>
	 * <li>The bit initially has the value true, and the corresponding bit in the argument has the value false.</li>
	 * <li>The bit initially has the value false, and the corresponding bit in the argument has the value true.</li>
	 * </ul>
	 * @param other */
	public void xor(BitVector other) {
		int commonWords = Math.min(words.length, other.words.length);

		for (int i = 0; commonWords > i; i++) {
			words[i] ^= other.words[i];
		}

		if (commonWords < other.words.length) {
			checkCapacity(other.words.length);
			for (int i = commonWords, s = other.words.length; s > i; i++) {
				words[i] = other.words[i];
			}
		}
	}

	/** Returns true if the specified BitVector has any bits set to true that are also set to true in this BitVector.
	 *
	 * @param other a bit set
	 * @return boolean indicating whether this bit set intersects the specified bit set */
	public boolean intersects(BitVector other) {
		long[] bits = this.words;
		long[] otherBits = other.words;
		for (int i = 0, s = Math.min(bits.length, otherBits.length); s > i; i++) {
			if ((bits[i] & otherBits[i]) != 0) {
				return true;
			}
		}
		return false;
	}

	/** Returns true if this bit set is a super set of the specified set,
	 *  i.e. it has all bits set to true that are also set to true
	 * in the specified BitVector.
	 *
	 * @param other a bit set
	 * @return boolean indicating whether this bit set is a super set of the specified set
	 * */
	public boolean containsAll(BitVector other) {
		long[] bits = this.words;
		long[] otherBits = other.words;
		int otherBitsLength = otherBits.length;
		int bitsLength = bits.length;

		for (int i = bitsLength; i < otherBitsLength; i++) {
			if (otherBits[i] != 0) {
				return false;
			}
		}

		for (int i = 0, s = Math.min(bitsLength, otherBitsLength); s > i; i++) {
			if ((bits[i] & otherBits[i]) != otherBits[i]) {
				return false;
			}
		}
		return true;
	}

	public int cardinality() {
		int count = 0;
		for (int i = 0; i < words.length; i++)
			count += Long.bitCount(words[i]);

		return count;
	}

	/**
	 * Decodes the set bits as integers. The destination
	 * {@link IntBag} is reset before the bits are transposed.
	 *
	 * @param out decoded ints end up here
	 * @return Same as out
	 */
	public IntBag toIntBag(IntBag out) {
		if (isEmpty()) {
			out.setSize(0);
			return out;
		}

		int count = prepareBag(out, 1);

		int[] data = out.getData();
		for (int i = 0, index = 0; count > index; i++) {
			long bitset = words[i];
			int wordBits = i << 6;
			while (bitset != 0) {
				long t = bitset & -bitset;
				data[index] = wordBits + Long.bitCount(t - 1);
				bitset ^= t;

				index++;
			}
		}

		return out;
	}

	/**
	 * Decodes the set bits as pairs of <code>entity id</code> and
	 * {@link World#compositionId(int) compositionId}. The
	 * destination{@link IntBag} is reset before the bits are
	 * transposed.
	 *
	 * @param out decoded ints end up here
	 * @return Same as out
	 */
	public IntBag toIntBagIdCid(ComponentManager cm, IntBag out) {
		if (isEmpty()) {
			out.setSize(0);
			return out;
		}

		int count = prepareBag(out, 2);

		int[] data = out.getData();
		for (int i = 0, index = 0; count > index; i++) {
			long bitset = words[i];
			int wordBits = i << 6;
			while (bitset != 0) {
				long t = bitset & -bitset;
				int id = wordBits + Long.bitCount(t - 1);
				data[index] = id;
				data[index + 1] = cm.getIdentity(id);
				index += 2;
				bitset ^= t;
			}
		}

		return out;
	}

	private int prepareBag(IntBag out, int elementsPerEntry) {
		int count = elementsPerEntry * cardinality();
		out.ensureCapacity(count);
		out.setSize(count);
		return count;
	}

	@Override
	public int hashCode() {
		final int word = length() >>> 6;
		int hash = 0;
		for (int i = 0; word >= i; i++) {
			hash = 127 * hash + (int) (words[i] ^ (words[i] >>> 32));
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		BitVector other = (BitVector) obj;
		long[] otherBits = other.words;

		int commonWords = Math.min(words.length, otherBits.length);
		for (int i = 0; commonWords > i; i++) {
			if (words[i] != otherBits[i])
				return false;
		}

		if (words.length == otherBits.length)
			return true;

		return length() == other.length();
	}

	@Override
	public String toString() {
		int cardinality = cardinality();
		int end = Math.min(128, cardinality);
		int count = 0;

		StringBuilder sb = new StringBuilder();
		sb.append("BitVector[").append(cardinality);
		if (cardinality > 0) {
			sb.append(": {");
			for (int i = nextSetBit(0); end > count && i != -1; i = nextSetBit(i + 1)) {
				if (count != 0)
					sb.append(", ");

				sb.append(i);
				count++;
			}

			if (cardinality > end)
				sb.append(" ...");

			sb.append("}");
		}
		sb.append("]");
		return sb.toString();
	}
}
