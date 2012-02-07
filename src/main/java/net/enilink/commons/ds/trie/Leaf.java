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

final class Leaf<V> extends Node<V> {
	V value;

	Leaf(SiString key, V value) {
		super(key);
		this.value = value;
	}

	public String toString() {
		return value + ": " + key;
	}

	boolean isLeaf() {
		return true;
	}

	@Override
	V getValue() {
		return value;
	}
}
