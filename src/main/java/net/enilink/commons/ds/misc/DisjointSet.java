package net.enilink.commons.ds.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple fast union-find tree data structure
 * 
 * @author Ken Wenzel
 * 
 * @param <E>
 *            type of set elements
 */
public class DisjointSet<E> {
	class Node {
		E element;
		Node parent = null;
		int count = 1;

		Node(E element) {
			this.element = element;
		}
	}

	int size;
	Map<E, Node> nodes = new HashMap<E, Node>();

	public E union(E a, E b) {
		Node aNode = findRoot(a);
		Node bNode = findRoot(b);

		if (aNode == bNode) {
			return aNode.element;
		} else if (aNode == null) {
			return bNode != null ? bNode.element : null;
		} else if (bNode == null) {
			return aNode != null ? aNode.element : null;
		}

		size--;

		if (aNode.count >= bNode.count) {
			bNode.parent = aNode;
			aNode.count += bNode.count;

			return aNode.element;
		} else {
			aNode.parent = bNode;
			bNode.count += aNode.count;

			return bNode.element;
		}
	}

	public E find(E element) {
		Node node = findRoot(element);
		return node != null ? node.element : null;
	}

	public boolean contains(E element) {
		return find(element) != null;
	}

	private Node findRoot(E element) {
		Node node = nodes.get(element);
		if (node == null) {
			return null;
		}
		return findRootNode(node);
	}

	private Node findRootNode(Node node) {
		// representative found
		if (node.parent == null) {
			return node;
		}
		Node parent = findRootNode(node.parent);
		// path compression
		if (node.parent != parent) {
			node.parent = parent;
		}
		return parent;
	}

	public void add(E element) {
		if (!nodes.containsKey(element)) {
			nodes.put(element, new Node(element));
			size++;
		}
	}

	public void addAll(Collection<? extends E> elements) {
		for (E element : elements) {
			if (!nodes.containsKey(element)) {
				nodes.put(element, new Node(element));
				size++;
			}
		}
	}

	public void clear() {
		nodes.clear();
	}

	public int size() {
		return size;
	}

	public Map<E, Set<E>> getSubSetMap() {
		Map<E, Set<E>> sets = new HashMap<E, Set<E>>();

		for (Node node : nodes.values()) {
			Node root = findRootNode(node);
			Set<E> set = sets.get(root.element);
			if (set == null) {
				set = new HashSet<E>();
				sets.put(root.element, set);
			}
			set.add(node.element);
		}

		return sets;
	}

	public Collection<Set<E>> getSubSets() {
		return getSubSetMap().values();
	}
}