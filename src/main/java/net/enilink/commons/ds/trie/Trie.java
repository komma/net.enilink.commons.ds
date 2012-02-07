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
 * Dynamic level-compressed binary tries.
 * <p>
 * 
 * This class implements an ordered dictionary that maps keys to values. Any
 * non-null object can be used as a value, but the keys must be sistrings,
 * semi-infinite binary strings.
 * 
 * @author Stefan.Nilsson@hut.fi
 * @version 1.0, 30 April 1998
 */

public class Trie<V> implements ITrie<V> {
	/***************************************************************************
	 * *************************************************** PRIVATE FIELDS *
	 * ***************************************************
	 */

	private Node<V> trie;
	private int size;

	/*
	 * If during an insert() or delete() it is found that the key is present in
	 * the trie, keyFound will be true and prevValue will contain the value
	 * associated with the key before the update.
	 */
	private boolean keyFound;
	private V prevValue;

	/***************************************************************************
	 * *************************************************** PUBLIC INTERFACE *
	 * ***************************************************
	 */

	/** Constructs a new empty trie. */
	public Trie() {
		trie = null;
		size = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhg.iwu.common.core.ds.trie.ITrie#put(de.fhg.iwu.common.core.ds.trie
	 * .SiString, V)
	 */
	public synchronized V put(SiString key, V value) {
		if (key == null || value == null)
			throw new NullPointerException();

		keyFound = false;
		trie = insert(key, value, trie, 0);
		if (keyFound) {
			return prevValue;
		} else {
			size++;
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhg.iwu.common.core.ds.trie.ITrie#get(de.fhg.iwu.common.core.ds.trie
	 * .SiString)
	 */

	public synchronized V get(SiString key) {
		Node<V> n = trie;

		while (n != null && !n.isLeaf()) {
			InternalNode<V> inode = (InternalNode<V>) n;
			n = inode.getChild(key.extractBits(inode.pos, inode.bits));
		}
		if (n == null) {
			return null;
		}
		return n.key.equals(key) ? n.getValue() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhg.iwu.common.core.ds.trie.ITrie#findPrefix(de.fhg.iwu.common.core
	 * .ds.trie.SiString)
	 */

	public synchronized V findPrefix(SiString key) {
		Node<V> n = trie;

		int childIndex = 0;
		Node<V> leftLeaf = null;
		while (n != null && !n.isLeaf()) {
			InternalNode<V> inode = (InternalNode<V>) n;

			childIndex = key.extractBits(inode.pos, inode.bits);
			n = inode.getChild(childIndex);

			if (childIndex > 0) {
				Node<V> leftNode = inode.getChild(childIndex - 1);
				if (leftNode.isLeaf()
						&& leftNode.key
								.subEquals(0, leftNode.key.length(), key)) {
					leftLeaf = leftNode;
				}
			}
		}
		if (n == null) {
			return null;
		}
		if (n.isLeaf()) {
			if (n.key.subEquals(0, n.key.length(), key)) {
				return n.getValue();
			} else if (leftLeaf != null) {
				return leftLeaf.getValue();
			}
		}
		return null;
	}

	/*
	 * Returns <code>true</code> if this trie contains no mappings.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.common.core.ds.trie.ITrie#isEmpty()
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhg.iwu.common.core.ds.trie.ITrie#remove(de.fhg.iwu.common.core.ds
	 * .trie.SiString)
	 */
	public synchronized Object remove(SiString key) {
		keyFound = false;
		trie = delete(key, trie);
		if (keyFound) {
			size--;
			return prevValue;
		} else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.common.core.ds.trie.ITrie#clear()
	 */
	public synchronized void clear() {
		trie = null;
		size = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.common.core.ds.trie.ITrie#size()
	 */
	public int size() {
		return size;
	}

	/**
	 * Return a string displaying the tree structure.
	 */
	synchronized String printDebug() {
		StringBuffer strbuf = new StringBuffer();
		int depth = 0;
		traverse(trie, depth, strbuf);
		return strbuf.toString();
	}

	/**
	 * Returns <code>true</code> if this trie is an LC-trie.
	 */
	boolean isLC() {
		return traverseAndCheckLC(trie);
	}

	/**
	 * Return a string displaying statistics about the trie.
	 */
	String printStat() {
		int bytes = 0; // How many bytes are used, a ref is 4 bytes

		StatStruct stat = new StatStruct();
		collectStat(trie, 0, stat);
		StringBuffer strbuf = new StringBuffer();
		String newline = System.getProperty("line.separator");
		if (isLC())
			strbuf.append("Complete LC-trie" + newline);
		strbuf.append("Aver depth: " + (float) stat.totDepth / this.size
				+ newline);
		strbuf.append("Max depth: " + stat.maxDepth + newline);
		strbuf.append("Leaves: " + stat.leaves + newline);
		bytes += 8 * stat.leaves;
		strbuf.append("Internal nodes: " + stat.internalNodes + newline);
		bytes += 28 * stat.internalNodes;
		int max = stat.nodeSizes.length - 1;
		while (max >= 0 && stat.nodeSizes[max] == 0)
			max--;
		int pointers = 0;
		for (int i = 1; i <= max; i++)
			if (stat.nodeSizes[i] != 0) {
				strbuf.append("  " + i + ": ");
				strbuf.append(stat.nodeSizes[i] + newline);
				pointers += (1 << i) * stat.nodeSizes[i];
			}
		strbuf.append("Pointers: " + pointers + newline);
		bytes += 4 * pointers;
		strbuf.append("Null ptrs: " + stat.nullPointers + newline);
		strbuf.append("Total size: " + bytes / 1000 + " kB" + newline);
		return strbuf.toString();
	}

	public static void main(String[] args) {
		ITrie<String> trie = new Trie<String>();
		String s = "www.tu";
		trie.put(new ByteString(s), s);

		s = "www.t";
		trie.put(new ByteString(s), s);

		s = "www.tu-chemnitz.de";
		trie.put(new ByteString(s), s);

		s = "www.tu-chemnitz.de/info+";
		trie.put(new ByteString(s), s);

		String value = trie
				.findPrefix(new ByteString("www.tu-chemnitz.de/info"));

		System.out.println(value);
	}

	/***************************************************************************
	 * *************************************************** PRIVATE METHODS *
	 * ***************************************************
	 */

	private Node<V> insert(SiString key, V value, Node<V> trie, int pos) {
		// Insert into an empty trie.
		if (trie == null) {
			Leaf<V> leaf = new Leaf<V>(key, value);
			return leaf;
		}

		// Insert into a subtrie.
		if (!trie.isLeaf()) {
			InternalNode<V> inode = (InternalNode<V>) trie;
			if (inode.key.subEquals(pos, inode.pos - pos, key)) {
				int bitpat = key.extractBits(inode.pos, inode.bits);
				Node<V> n = insert(key, value, inode.getChild(bitpat),
						inode.pos + inode.bits);
				inode.putChild(bitpat, n);
				return inode.resize();
			}
		}

		// The string is already in the trie.
		if (trie.isLeaf() && key.equals(trie.key)) {
			keyFound = true;
			Leaf<V> leaf = (Leaf<V>) trie;
			prevValue = leaf.value;
			leaf.value = value;
			return trie;
		}

		// Add a new node here.
		int newpos = key.misMatch(pos, trie.key);
		InternalNode<V> node = new InternalNode<V>(trie.key, newpos, 1);
		Leaf<V> leaf = new Leaf<V>(key, value);
		if (key.extractBits(newpos, 1) == 0) {
			node.putChild(0, leaf);
			node.putChild(1, trie);
		} else {
			node.putChild(0, trie);
			node.putChild(1, leaf);
		}
		return node.resize();
	}

	private Node<V> delete(SiString key, Node<V> t) {
		if (t == null) {
			return null;
		}
		if (t.isLeaf()) {
			Leaf<V> leaf = (Leaf<V>) t;
			if (leaf.key.equals(key)) {
				keyFound = true;
				prevValue = leaf.value;
				return null;
			}
		} else {
			InternalNode<V> inode = (InternalNode<V>) t;
			int bits = key.extractBits(inode.pos, inode.bits);
			inode.putChild(bits, delete(key, inode.getChild(bits)));
			t = inode.resize();
		}
		return t;
	}

	private void traverse(Node<V> trie, int depth, StringBuffer strbuf) {
		String newline = System.getProperty("line.separator");
		for (int i = 0; i < depth; i++)
			strbuf.append("    ");
		if (trie == null)
			strbuf.append("null" + newline);
		else if (trie.isLeaf())
			strbuf.append(trie + newline);
		else {
			InternalNode<V> inode = (InternalNode<V>) trie;
			strbuf.append(inode + newline);
			for (int i = 0; i < 1 << inode.bits; i++)
				traverse(inode.getChild(i), depth + 1, strbuf);
		}
	}

	private boolean traverseAndCheckLC(Node<V> trie) {
		if (trie == null || trie.isLeaf()) {
			return true;
		} else {
			InternalNode<V> inode = (InternalNode<V>) trie;
			if (!inode.isLC()) {
				return false;
			}
			for (int i = 0; i < 1 << inode.bits; i++) {
				if (!traverseAndCheckLC(inode.getChild(i))) {
					return false;
				}
			}
			return true;
		}
	}

	private class StatStruct {
		int totDepth = 0;
		int maxDepth = 0;
		int internalNodes = 0;
		int leaves = 0;
		int nullPointers = 0;
		int[] nodeSizes = new int[32];
	}

	private void collectStat(Node<V> trie, int depth, StatStruct stat) {
		if (trie == null) {
			stat.nullPointers++;
		} else if (trie.isLeaf()) {
			if (depth > stat.maxDepth) {
				stat.maxDepth = depth;
			}
			stat.totDepth += depth;
			stat.leaves++;
		} else {
			InternalNode<V> inode = (InternalNode<V>) trie;
			stat.internalNodes++;
			stat.nodeSizes[inode.bits]++;
			for (int i = 0; i < 1 << inode.bits; i++) {
				collectStat(inode.getChild(i), depth + 1, stat);
			}
		}
	}
}
