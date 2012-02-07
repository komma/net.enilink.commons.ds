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
 * A semi-infinite binary string consisting of two interleaved 32 bit integers.
 * The strings are conceptually padded with zeroes.
 */
final public class Interleaved2DPoint implements SiString {
	// int x, y; // The original data point
	long data; // The interleaved bits

	/*
	 * MIX is a function that mixes the first 8 bits of a 16-bit bitstring with
	 * the last 8 bits. E.g: MIX[1111111100000000] = 1010101010101010 UNMIX is
	 * the inverse function.
	 */
	static short[] MIX = new short[65536];
	// static short[] UNMIX = new short[65536];

	static {
		for (int i = 0; i < MIX.length; i++) {
			int res = 0;
			for (int j = 0; j < 8; j++)
				res |= (i >>> j & 1) << (2 * j);
			for (int j = 8; j < 16; j++)
				res |= (i >>> j & 1) << (2 * j - 15);
			MIX[i] = (short) res;
		}
		// for (int i = 0; i < MIX.length; i++)
		// UNMIX[((int) MIX[i]) & 0xFFFFD] = i;
	}

	Interleaved2DPoint(int x, int y) {
		// this.x = x;
		// this.y = y;
		data = (((long) MIX[x >>> 24 << 8 | y >>> 24]) & 0xFFFFL) << 48;
		data |= (((long) MIX[x << 8 >>> 24 << 8 | y << 8 >>> 24]) & 0xFFFFL) << 32;
		data |= (((long) MIX[x << 16 >>> 24 << 8 | y << 16 >>> 24]) & 0xFFFFL) << 16;
		data |= ((long) MIX[x << 24 >>> 16 | y << 24 >>> 24]) & 0xFFFFL;
	}

	public boolean equals(SiString s) {
		Interleaved2DPoint p = (Interleaved2DPoint) s;
		return p.data == data;
	}

	// We don't care about the sign bit: Coordinates are positive
	// and hence the sign bit is 0.
	public int compareTo(SiString s) {
		Interleaved2DPoint p = (Interleaved2DPoint) s;
		if (p.data < data)
			return -1;
		if (p.data > data)
			return 1;
		return 0;
	}

	/**
	 * Try to match the part of both strings that start at <code>offset</code>
	 * and has length <code>bits</code>, where
	 * <code>offset/bits> and <code>bits</code> are nonnegative.
	 */
	public boolean subEquals(int offset, int bits, SiString s) {
		if (bits == 0 || offset >= 64)
			return true;
		Interleaved2DPoint p = (Interleaved2DPoint) s;
		return (p.data ^ data) << offset >>> (64 - bits) == 0;
	}

	/**
	 * Find the first mismatch starting at <code>offset</code>. The strings are
	 * supposed to be different!
	 */
	public int misMatch(int offset, SiString s) {
		Interleaved2DPoint p = (Interleaved2DPoint) s;
		long diff = (p.data ^ data) << offset;
		return offset + firstOne(diff);
	}

	/**
	 * 1 <= <code>bits</code> <= 32
	 */
	public int extractBits(int offset, int bits) {
		if (offset >= 64)
			return 0;
		else
			return (int) (data << offset >>> (64 - bits));

	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < 64; i++) {
			if (i % 8 == 0 && i != 0)
				buf.append(" ");
			buf.append(data >>> 63 - i & 1);
		}
		return buf.toString();
	}

	/*
	 * Return the position of the first bit in i, i != 0, that is one.
	 */
	private final int firstOne(long n) {
		final int firstTable[] = { -1, 3, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
				0, 0 };
		int pos = 0;
		int size = 32;

		while (size > 2) {
			long left = n >>> size;
			if (left != 0)
				n = left;
			else
				pos += size;
			size >>= 1;
		}
		return pos + firstTable[(int) n];
	}

	public static void main(String[] args) {
		// SELF TEST
	}

	public int length() {
		return 64;
	}
}
