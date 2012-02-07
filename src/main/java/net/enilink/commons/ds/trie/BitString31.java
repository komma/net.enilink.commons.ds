package net.enilink.commons.ds.trie;

/*
 *  The code presented in this file has been tested with
 *  care but is not guaranteed for any purpose. The writer
 *  does not offer any warranties nor does he accept any
 *  liabilities with respect to the code.
 *
 *  Stefan.Nilsson@hut.fi
 *  Department of Computer Science
 *  Helsinki University of Technology
 */

/**
 * A semi-infinite binary string. Conceptuelly the infinite string '100000...'
 * has been added to the end of the string. Currently only strings of length at
 * most 31 are supported.
 */
final public class BitString31 implements SiString {
	int data = 0; // The unused bits must be zero.
	int length; // # bits in string

	BitString31(int data, int length) {
		if (length > 0)
			this.data = data;
		this.length = length;
		// remove garbage at the end of data. (Problems when length == 0!)
		this.data = this.data >>> (32 - length) << (32 - length);
		// add 1 at the end
		this.data |= 1 << (31 - length);
	}

	BitString31(String bits) {
		int data = 0;
		for (int i = 0; i < bits.length(); i++)
			if (bits.charAt(i) == '1')
				data |= 1 << (31 - i);
		this.data = data;
		this.length = bits.length();
		// add 1 at the end
		this.data |= 1 << (31 - length);
	}

	public boolean equals(SiString s) {
		if (((BitString31) s).length == length) {
			return data == ((BitString31) s).data;
		} else
			return false;
	}

	// This method compares signed integers, not bit strings.
	public int compareTo(SiString s) {
		if (data < ((BitString31) s).data)
			return -1;
		else if (data > ((BitString31) s).data)
			return 1;
		else
			return 0;
	}

	/**
	 * Try to match the part of both strings that start at <code>offset</code>
	 * and has length <code>bits</code>, where
	 * <code>offset/bits> and <code>bits</code> are nonnegative.
	 */
	public boolean subEquals(int offset, int bits, SiString s) {
		if (bits == 0 || offset >= 32)
			return true;
		bits = bits > 32 ? 32 : bits;
		return (((BitString31) s).data ^ data) << offset >>> (32 - bits) == 0;
	}

	/**
	 * Find the first mismatch starting at <code>offset</code>. The strings are
	 * supposed to be different!
	 */
	public int misMatch(int offset, SiString s) {
		int diff = ((BitString31) s).data ^ data;
		int i = offset;
		while (diff << i >>> 31 == 0)
			i++;
		return i;
	}

	/**
	 * 1 <= <code>bits</code> <= 32
	 */
	public int extractBits(int offset, int bits) {
		if (offset < 32)
			return data << offset >>> (32 - bits);
		else
			return 0;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("\"");
		for (int i = 0; i < length; i++) {
			if (i % 8 == 0 && i != 0)
				buf.append(" ");
			buf.append(data >>> 31 - i & 1);
		}
		buf.append("\"");
		return buf.toString();
	}

	public int length() {
		return length;
	}
}
