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
 * A semi-infinite binary string. When implementing this interface you must make
 * sure that no string is a proper prefix of anonther string. Also, you must
 * allow for misMatch() and extractBits() to 'read' bits in any position greater
 * to or equal to zero. There are several ways to achieve this.
 * <p>
 * 
 * 1. If all the strings are of equal length, you can just pad the strings will
 * zeroes.
 * <p>
 * 
 * 2. For non-binary strings you may add a special end marker to each string
 * (and pad with zeroes).
 * <p>
 * 
 * 3. You may also pad the strings with the infinite string '10000000....'. (The
 * '1' may or may not be part of the actual representation.)
 */
interface SiString {
	/**
	 * Are the two strings equal? That is, do they have the same length and the
	 * same bit pattern.
	 */
	public boolean equals(SiString s);

	/**
	 * Try to match the part of both strings that start at <code>offset</code>
	 * and has length <code>bits</code>, where
	 * <code>offset/bits> and <code>bits</code> are nonnegative.
	 */
	public boolean subEquals(int offset, int bits, SiString s);

	/**
	 * Find the first mismatch starting at <code>offset</code>. We know that
	 * there is a mismatch.
	 */
	public int misMatch(int offset, SiString s);

	/**
	 * Extract a number of bits and return the value as an integer. 1 <=
	 * <code>bits</code> <= 32
	 */
	public int extractBits(int offset, int bits);

	/**
	 * This method is not used in a trie. It's included only for the comparison
	 * with binary trees. Return the value 0 if this object equals the argument;
	 * a value less than 0 if this object is less than the argument; and a value
	 * larger than 0 if this object is larger than the argument.
	 */
	public int compareTo(SiString s);

	/**
	 * Length of this string
	 */
	public int length();
}
