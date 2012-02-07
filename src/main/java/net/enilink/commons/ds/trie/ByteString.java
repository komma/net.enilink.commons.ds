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
 * A semi-infinite string of bytes. Zero is used as a unique endmarker and the
 * strings are conceptually padded with zeroes.
 */
final public class ByteString implements SiString {
	int[] data; // The trailing unused bits must be zero.
	int length; // Number of 8-bit characters in string

	ByteString(String str) {
		str = removeNull(str);
		length = str.length();

		byte[] byteArray = str.getBytes();
		if (length > 0)
			data = new int[1 + ((length - 1) >> 2)];
		else
			data = new int[0];
		for (int i = 0; i < length; i++)
			putByte(byteArray[i], i);
	}

	public boolean equals(SiString s) {
		ByteString str = (ByteString) s;
		if (data.length == str.data.length) {
			for (int i = 0; i < data.length; i++)
				if (data[i] != str.data[i])
					return false;
			return true;
		} else
			return false;
	}

	public int compareTo(SiString s) {
		ByteString str = (ByteString) s;
		int min;
		if (data.length < str.data.length)
			min = data.length;
		else
			min = str.data.length;
		for (int i = 0; i < min; i++)
			if (data[i] < str.data[i])
				return -1;
			else if (data[i] > str.data[i])
				return 1;
		if (data.length < str.data.length)
			return -1;
		else if (data.length > str.data.length)
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
		int diff; // The XOR difference betwen two bitpatterns

		if (bits == 0)
			return true;

		int firstWord = offset >>> 5;
		int firstPos = offset & 037;
		ByteString str = (ByteString) s;

		diff = (getWord(data, firstWord) ^ getWord(str.data, firstWord));
		if (firstPos + bits <= 32)
			diff = diff << firstPos >>> (32 - bits);
		else
			diff = diff << firstPos;
		if (diff != 0)
			return false;

		int lastWord = (offset + bits - 1) >>> 5;
		for (int i = firstWord + 1; i < lastWord; i++)
			if (getWord(data, i) != getWord(str.data, i))
				return false;

		if (lastWord > firstWord) {
			int lastPos = (offset + bits - 1) & 037;
			diff = (getWord(data, lastWord) ^ getWord(str.data, lastWord)) >>> (31 - lastPos);
			if (diff != 0)
				return false;
		}

		return true;
	}

	/**
	 * Find the first mismatch starting at <code>offset</code>. The strings are
	 * supposed to be different!
	 */
	public int misMatch(int offset, SiString s) {
		int diff; // The XOR difference between two bitpatterns

		int firstWord = offset >>> 5;
		int firstPos = offset & 037;
		ByteString str = (ByteString) s;
		diff = (getWord(data, firstWord) ^ getWord(str.data, firstWord)) << firstPos;
		if (diff != 0)
			return offset + firstOne(diff);

		int i = firstWord;
		while (diff == 0) {
			i++;
			diff = getWord(data, i) ^ getWord(str.data, i);
		}
		return (i << 5) + firstOne(diff);
	}

	/**
	 * 1 <= <code>bits</code> <= 32
	 */
	public int extractBits(int offset, int bits) {
		int word = offset >>> 5;
		int pos = offset & 037;
		if (pos + bits <= 32)
			return getWord(data, word) << pos >>> (32 - bits);
		else
			return getWord(data, word) << pos >>> (32 - bits)
					| getWord(data, word + 1) >>> (64 - pos - bits);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("\"");
		/*
		 * for (int i = 0; i < length; i++) { for (int j = 0; j < 8; j++)
		 * buf.append((char) getByte(data, i) >>> 7 - j & 1); buf.append(" "); }
		 */
		for (int i = 0; i < length; i++)
			buf.append((char) getByte(data, i));
		buf.append("\"");
		return buf.toString();
	}

	/*
	 * Remove all null characters from string.
	 */
	private final String removeNull(String str) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch != '\000')
				strBuf.append(ch);
		}
		return strBuf.toString();
	}

	/*
	 * Put the byte b into position i in data.
	 */
	private final void putByte(byte b, int i) {
		data[i >> 2] |= b << ((3 - (i & 03)) << 3);
	}

	/*
	 * Get byte position i in data.
	 */
	private final int getByte(int[] data, int i) {
		if (i >> 2 < data.length)
			return data[i >> 2] >>> ((3 - (i & 03)) << 3) & 0377;
		else
			return 0;
	}

	/*
	 * Get word in position i in data.
	 */
	private final int getWord(int[] data, int i) {
		if (i < data.length)
			return data[i];
		else
			return 0;
	}

	/*
	 * Return the position of the first bit in i, i != 0, that is one.
	 */
	private final int firstOne(int n) {
		final int firstTable[] = { -1, 3, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0,
				0, 0 };
		int pos = 0;
		int size = 16;

		while (size > 2) {
			int left = n >>> size;
			if (left != 0)
				n = left;
			else
				pos += size;
			size >>= 1;
		}
		return pos + firstTable[n];
	}

	public static void main(String[] args) {
		// SELF TEST
	}

	public int length() {
		return length * 8;
	}

}
