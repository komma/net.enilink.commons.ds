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

abstract class Node<V> {
	/**
	 * Every node points to a string. An internal node points to any string in
	 * its subtrie.
	 */
	SiString key;

	Node(SiString key) {
		this.key = key;
	}

	abstract boolean isLeaf();

	V getValue() {
		return null;
	}
}