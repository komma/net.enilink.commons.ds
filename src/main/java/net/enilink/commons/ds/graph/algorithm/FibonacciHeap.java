/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is GraphMaker. The Initial Developer of the Original
 * Software is Nathan L. Fiedler. Portions created by Nathan L. Fiedler
 * are Copyright (C) 1999-2008. All Rights Reserved.
 *
 * Contributor(s): Nathan L. Fiedler.
 *
 * $Id$
 */
package net.enilink.commons.ds.graph.algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements a Fibonacci heap data structure. Much of the code in
 * this class is based on the algorithms in Chapter 21 of the
 * "Introduction to Algorithms" by Cormen, Leiserson, Rivest, and Stein. The
 * amortized running time of most of these methods is O(1), making it a very
 * fast data structure. Several have an actual running time of O(1). removeMin()
 * and delete() have O(log n) amortized running times because they do the heap
 * consolidation.
 * 
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access a set concurrently, and at least one of the threads
 * modifies the set, it <em>must</em> be synchronized externally. This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the set.
 * </p>
 * 
 * @author Nathan Fiedler
 */
public class FibonacciHeap<T> {
	private Comparator<? super T> comparator;

	/** Points to the minimum node in the heap. */
	private Node<T> min;
	/**
	 * Number of nodes in the heap. If the type is ever widened, (e.g. changed
	 * to long) then recalcuate the maximum degree value used in the
	 * consolidate() method.
	 */
	private int n;

	private Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();

	public FibonacciHeap(Comparator<? super T> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Performs a cascading cut operation. This cuts y from its parent and then
	 * does the same for its parent, and so on up the tree.
	 * 
	 * <p>
	 * <em>Running time: O(log n)</em>
	 * </p>
	 * 
	 * @param y
	 *            node to perform cascading cut on
	 */
	private void cascadingCut(Node<T> y) {
		Node<T> z = y.parent;
		// if there's a parent...
		if (z != null) {
			if (y.mark) {
				// it's marked, cut it from parent
				cut(y, z);
				// cut its parent as well
				cascadingCut(z);
			} else {
				// if y is unmarked, set it marked
				y.mark = true;
			}
		}
	}

	/**
	 * Removes all elements from this heap.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 */
	public void clear() {
		min = null;
		n = 0;
	}

	/**
	 * Consolidates the trees in the heap by joining trees of equal degree until
	 * there are no more trees of equal degree in the root list.
	 * 
	 * <p>
	 * <em>Running time: O(log n) amortized</em>
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	private void consolidate() {
		// The magic 45 comes from log base phi of Integer.MAX_VALUE,
		// which is the most elements we will ever hold, and log base
		// phi represents the largest degree of any root list node.
		Node[] A = new Node[45];

		// For each root list node look for others of the same degree.
		Node<T> start = min;
		Node<T> w = min;
		do {
			Node<T> x = w;
			// Because x might be moved, save its sibling now.
			Node<T> nextW = w.right;
			int d = x.degree;
			while (A[d] != null) {
				// Make one of the nodes a child of the other.
				Node<T> y = A[d];
				if (comparator.compare(x.data, y.data) > 0) {
					Node<T> temp = y;
					y = x;
					x = temp;
				}
				if (y == start) {
					// Because removeMin() arbitrarily assigned the min
					// reference, we have to ensure we do not miss the
					// end of the root node list.
					start = start.right;
				}
				if (y == nextW) {
					// If we wrapped around we need to check for this case.
					nextW = nextW.right;
				}
				// Node y disappears from root list.
				link(y, x);
				// We've handled this degree, go to next one.
				A[d] = null;
				d++;
			}
			// Save this node for later when we might encounter another
			// of the same degree.
			A[d] = x;
			// Move forward through list.
			w = nextW;
		} while (w != start);

		// Find the minimum key again.
		for (Node a : A) {
			if (a != null && a.compareTo(min) < 0) {
				min = a;
			}
		}
	}

	/**
	 * The reverse of the link operation: removes x from the child list of y.
	 * This method assumes that min is non-null.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @param x
	 *            child of y to be removed from y's child list
	 * @param y
	 *            parent of x about to lose a child
	 */
	private void cut(Node<T> x, Node<T> y) {
		// remove x from childlist of y and decrement degree[y]
		x.left.right = x.right;
		x.right.left = x.left;
		y.degree--;
		// reset y.child if necessary
		if (y.degree == 0) {
			y.child = null;
		} else if (y.child == x) {
			y.child = x.right;
		}
		// add x to root list of heap
		x.right = min;
		x.left = min.left;
		min.left = x;
		x.left.right = x;
		// set parent[x] to nil
		x.parent = null;
		// set mark[x] to false
		x.mark = false;
	}

	/**
	 * Decreases the key value for a heap node, given the new value to take on.
	 * The structure of the heap may be changed, but will not be consolidated.
	 * 
	 * <p>
	 * <em>Running time: O(1) amortized</em>
	 * </p>
	 * 
	 * @param x
	 *            node to decrease the key of
	 * @exception IllegalArgumentException
	 *                if k is larger than x.key value.
	 */
	public void decreaseKey(T x) {
		Node<T> node = nodeMap.get(x);
		if (node != null) {
			return;
		}
		
		decreaseKey(node, false);
	}

	/**
	 * Decrease the key value of a node, or simply bubble it up to the top of
	 * the heap in preparation for a delete operation.
	 * 
	 * @param x
	 *            node to decrease the key of.
	 * @param delete
	 *            true if deleting node (in which case, k is ignored).
	 */
	private void decreaseKey(Node<T> x, boolean delete) {
		// if (!delete && k > x.key) {
		// throw new IllegalArgumentException("cannot increase key value");
		// }
		// x.key = k;
		Node<T> y = x.parent;
		if (y != null && (delete || x.compareTo(y) < 0)) {
			cut(x, y);
			cascadingCut(y);
		}
		if (delete || x.compareTo(min) < 0) {
			min = x;
		}
	}

	/**
	 * Deletes a node from the heap given the reference to the node. The trees
	 * in the heap will be consolidated, if necessary.
	 * 
	 * <p>
	 * <em>Running time: O(log n) amortized</em>
	 * </p>
	 * 
	 * @param x
	 *            node to remove from heap.
	 */
	public void delete(T x) {
		Node<T> node = nodeMap.get(x);
		if (node != null) {
			return;
		}

		// make x as small as possible
		decreaseKey(node, true);
		// remove the smallest, which decreases n also
		removeMin();
	}

	/**
	 * Tests if the Fibonacci heap is empty or not. Returns true if the heap is
	 * empty, false otherwise.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @return true if the heap is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return min == null;
	}

	/**
	 * Inserts a new data element into the heap. No heap consolidation is
	 * performed at this time, the new node is simply inserted into the root
	 * list of this heap.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @param x
	 *            data object to insert into heap.
	 * @return newly created heap node.
	 */
	public boolean insert(T x) {
		Node<T> node = nodeMap.get(x);
		if (node != null) {
			return false;
		}
		node = new Node<T>(x, comparator);
		nodeMap.put(x, node);

		// concatenate node into min list
		if (min != null) {
			node.right = min;
			node.left = min.left;
			min.left = node;
			node.left.right = node;
			if (node.compareTo(min) < 0) {
				min = node;
			}
		} else {
			min = node;
		}
		n++;

		return true;
	}

	/**
	 * Make node y a child of node x.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @param y
	 *            node to become child
	 * @param x
	 *            node to become parent
	 */
	private void link(Node<T> y, Node<T> x) {
		// remove y from its circular list
		y.left.right = y.right;
		y.right.left = y.left;
		// make y a child of x
		y.parent = x;
		if (x.child == null) {
			x.child = y;
			y.right = y;
			y.left = y;
		} else {
			y.left = x.child;
			y.right = x.child.right;
			x.child.right = y;
			y.right.left = y;
		}
		// increase degree[x]
		x.degree++;
		// set mark[y] false
		y.mark = false;
	}

	/**
	 * Returns the smallest element in the heap. This smallest element is the
	 * one with the minimum key value.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @return heap node with the smallest key, or null if empty.
	 */
	public Node<T> min() {
		return min;
	}

	/**
	 * Removes the smallest element from the heap. This will cause the trees in
	 * the heap to be consolidated, if necessary.
	 * 
	 * <p>
	 * <em>Running time: O(log n) amortized</em>
	 * </p>
	 * 
	 * @return data object with the smallest key.
	 * @throws NoSuchElementException
	 *             if the heap is empty.
	 */
	public T removeMin() {
		Node<T> z = min;
		if (z == null) {
			return null;
		}
		// for each child of z do...
		if (z.child != null) {
			// set parent[x] to null
			z.child.parent = null;
			for (Node<T> x = z.child.right; x != z.child; x = x.right) {
				x.parent = null;
			}
			// merge the children into root list
			Node<T> minleft = min.left;
			Node<T> zchildleft = z.child.left;
			min.left = zchildleft;
			zchildleft.right = min;
			z.child.left = minleft;
			minleft.right = z.child;
		}
		// remove z from root list of heap
		z.left.right = z.right;
		z.right.left = z.left;
		if (z == z.right) {
			min = null;
		} else {
			min = z.right;
			consolidate();
		}
		// decrement size of heap
		n--;
		return z.data;
	}

	/**
	 * Returns the size of the heap which is measured in the number of elements
	 * contained in the heap.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @return number of elements in the heap.
	 */
	public int size() {
		return n;
	}

	/**
	 * Joins two Fibonacci heaps into a new one. No heap consolidation is
	 * performed at this time. The two root lists are simply joined together.
	 * 
	 * <p>
	 * <em>Running time: O(1)</em>
	 * </p>
	 * 
	 * @param h
	 *            other heap
	 * @return new heap containing this heap and h
	 */
	public FibonacciHeap<T> union(FibonacciHeap<T> h) {
		FibonacciHeap<T> newHeap = new FibonacciHeap<T>(comparator);
		if (h != null) {
			newHeap.min = min;
			if (newHeap.min != null) {
				if (h.min != null) {
					newHeap.min.right.left = h.min.left;
					h.min.left.right = newHeap.min.right;
					newHeap.min.right = h.min;
					h.min.left = newHeap.min;
					if (h.min.compareTo(min) < 0) {
						newHeap.min = h.min;
					}
				}
			} else {
				newHeap.min = h.min;
			}
			newHeap.n = n + h.n;
		}
		return newHeap;
	}

	/**
	 * Implements a node of the Fibonacci heap. It holds the information
	 * necessary for maintaining the structure of the heap. It acts as an opaque
	 * handle for the data element, and serves as the key to retrieving the data
	 * from the heap.
	 * 
	 * @author Nathan Fiedler
	 */
	protected static class Node<T> implements Comparable<Node<T>> {
		/** Data object for this node, holds the key value. */
		private T data;
		/** Parent node. */
		private Node<T> parent;
		/** First child node. */
		private Node<T> child;
		/** Right sibling node. */
		private Node<T> right;
		/** Left sibling node. */
		private Node<T> left;
		/** Number of children of this node. */
		private int degree;
		/**
		 * True if this node has had a child removed since this node was added
		 * to its parent.
		 */
		private boolean mark;

		private Comparator<? super T> comparator;

		/**
		 * Two-arg constructor which sets the data and key fields to the passed
		 * arguments. It also initializes the right and left pointers, making
		 * this a circular doubly-linked list.
		 * 
		 * @param data
		 *            data object to associate with this node
		 */
		public Node(T data, Comparator<? super T> comparator) {
			this.data = data;
			this.right = this;
			this.left = this;
			this.comparator = comparator;
		}

		@Override
		public int compareTo(Node<T> other) {
			return comparator.compare(data, other.data);
		}
	}
}
