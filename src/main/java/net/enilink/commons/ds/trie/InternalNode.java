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

final class InternalNode<V> extends Node<V> {
	// The first bit represented by this node.
	int pos;

	// The number of bits used for branching, bits >= 1.
	int bits;

	// The size of this array is 2^bits.
	private Node<V>[] child;

	// How many children are internal nodes with an empty prefix?
	private int fullChildren;

	// How many children are nullpointers.
	private int emptyChildren;

	// The thresholds (in per cent) for inflating and halving
	private static int halveThreshold = 25;
	private static int inflateThreshold = 50;

	// Should the resize operation be performed recursively
	// Not yet implemented
	private static boolean resizeRecursively = true;

	/*
	 * PUBLIC INTERFACE
	 */

	static void setLowThreshold(int n) {
		if (0 <= n && n <= 100)
			halveThreshold = n;
	}

	static void setHighThreshold(int n) {
		if (0 <= n && n <= 100)
			inflateThreshold = n;
	}

	/* Not yet implemented */
	static void setResizeRecursively(boolean b) {
		resizeRecursively = b;
	}

	@SuppressWarnings("unchecked")
	InternalNode(SiString str, int pos, int bits) {
		super(str);
		this.pos = pos;
		this.bits = bits;
		child = new Node[1 << bits];
		fullChildren = 0;
		emptyChildren = child.length;
	}

	/*
	 * Add a child at position i overwriting the old value. Update the value of
	 * fullChildren and emptyChildren.
	 */
	@SuppressWarnings("unchecked")
	void putChild(int i, Node n) {
		Node chi = child[i];

		// update emptyChildren
		if (n == null && chi != null)
			emptyChildren++;
		else if (n != null && chi == null)
			emptyChildren--;

		// update fullChildren
		boolean wasFull = full(chi);
		boolean isFull = full(n);
		if (wasFull && !isFull)
			fullChildren--;
		else if (!wasFull && isFull)
			fullChildren++;

		child[i] = n;
	}

	@SuppressWarnings("unchecked")
	Node<V> getChild(int i) {
		return child[i];
	}

	/*
	 * If appropriate resize the node.
	 */
	@SuppressWarnings("unchecked")
	Node<V> resize() {
		// No children
		if (emptyChildren == child.length)
			return null;

		// One child
		if (emptyChildren == child.length - 1)
			for (int i = 0; i < child.length; i++)
				if (child[i] != null)
					return child[i];

		// To simulate Patricia, remove the following code.

		// Double as long as the resulting node has a number of
		// nonempty nodes that are above the threshold.
		while (fullChildren > 0
				&& 50 * (fullChildren + child.length - emptyChildren) >= inflateThreshold
						* child.length)
			inflate();

		// Halve as long as the numer of empty children in this
		// node is above threshold.
		while (bits > 1
				&& 100 * (child.length - emptyChildren) < halveThreshold
						* child.length)
			halve();

		// Only one child remains.
		if (emptyChildren == child.length - 1)
			for (int i = 0; i < child.length; i++)
				if (child[i] != null)
					return child[i];

		// To simulate Patricia, remove the previous code.

		return this;
	}

	/*
	 * It this a node in an LC-trie?
	 */
	boolean isLC() {
		return fullChildren < child.length && emptyChildren == 0;
	}

	public String toString() {
		return "[" + pos + "," + (1 << bits) + "] (" + fullChildren + ","
				+ emptyChildren + ")";
	}

	/*
	 * PRIVATE METHODS
	 */

	/*
	 * Check if a child is full, i.e. the child is an internal node and no bits
	 * are skipped.
	 */
	private boolean full(Node<V> child) {
		if (child == null || child instanceof Leaf)
			return false;
		return ((InternalNode<?>) child).pos == pos + bits;
	}

	/*
	 * Make sure that key points to a leaf in a subtrie.
	 */
	private void refreshKey() {
		for (int i = 0; i < child.length; i++)
			if (child[i] != null) {
				key = child[i].key;
				continue;
			}
	}

	@SuppressWarnings("unchecked")
	private void inflate() {
		Node[] oldChild = child;
		bits++;
		child = new Node[1 << bits];
		fullChildren = 0;
		emptyChildren = child.length;

		for (int i = 0; i < oldChild.length; i++) {
			Node node = oldChild[i];

			// An empty child
			if (node == null)
				continue;

			// A leaf or an internal node with skipped bits
			if (node.isLeaf() || ((InternalNode) node).pos > pos + bits - 1) {
				if (node.key.extractBits(pos + bits - 1, 1) == 0)
					putChild(2 * i, node);
				else
					putChild(2 * i + 1, node);
				continue;
			}

			// An internal node with two children
			InternalNode inode = (InternalNode) node;
			if (inode.bits == 1) {
				putChild(2 * i, inode.child[0]);
				putChild(2 * i + 1, inode.child[1]);

				// An internal node with more than two children
			} else {
				InternalNode left, right;
				left = new InternalNode(null, inode.pos + 1, inode.bits - 1);
				right = new InternalNode(null, inode.pos + 1, inode.bits - 1);
				int size = left.child.length;
				for (int j = 0; j < size; j++)
					left.putChild(j, inode.child[j]);
				for (int j = 0; j < size; j++)
					right.putChild(j, inode.child[j + size]);
				left.refreshKey();
				right.refreshKey();
				putChild(2 * i, left.resize());
				putChild(2 * i + 1, right.resize());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void halve() {
		Node<?>[] oldChild = child;
		bits--;
		child = new Node[1 << bits];
		fullChildren = 0;
		emptyChildren = child.length;
		for (int i = 0; i < oldChild.length; i += 2) {
			Node<?> left = oldChild[i];
			Node<?> right = oldChild[i + 1];

			// At least one of the children is empty.
			if (left == null) {
				if (right == null) // Both are empty
					continue;
				putChild(i / 2, right);
			} else if (right == null)
				putChild(i / 2, left);

			// Two nonempty children
			else {
				InternalNode<V> newBinNode = new InternalNode<V>(left.key, pos
						+ bits, 1);
				newBinNode.putChild(0, left);
				newBinNode.putChild(1, right);
				putChild(i / 2, newBinNode.resize());
			}
		}
	}

	protected boolean isLeaf() {
		return false;
	}
}
