package net.enilink.commons.ds.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hashmap based implementation of the {@link IGraph} interface.
 * 
 */
public class HashGraph<N, E> implements IGraph<N, E> {
	protected Map<N, GraphNode<N, E>> nodeMap;

	public HashGraph() {
		nodeMap = new LinkedHashMap<N, GraphNode<N, E>>();
	}

	public HashGraph(IGraph<N, E> original) {
		this();
		for (N node : original.getNodes()) {
			addNode(node);
			for (IEdge<N, E> edge : original.getOutEdges(node)) {
				addNode(edge.getEnd());
				addEdge(edge.getData(), node, edge.getEnd());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#addNode(T)
	 */
	public boolean addNode(N n) {
		if (nodeMap.containsKey(n))
			return false;
		nodeMap.put(n, new GraphNode<N, E>(n));
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#addEdge(E)
	 */
	public boolean addEdge(E edge, N pred, N succ) {
		GraphNode<N, E> node1 = nodeMap.get(pred), node2 = nodeMap.get(succ);

		if (node1 == null || node2 == null)
			return false;
		return node1.addOutEdge(edge, node2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#hasEdge(E)
	 */
	public boolean containsEdge(E edge, N pred, N succ) {
		GraphNode<N, E> node1 = nodeMap.get(pred);

		if (node1 != null) {
			for (Edge<N, E> e : node1.outEdges) {
				if (e.data == edge || (e.data != null && e.data.equals(edge))) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#getSuccessors(T)
	 */
	public Collection<N> getSuccessors(N node) {
		GraphNode<N, E> node1 = nodeMap.get(node);

		if (node1 != null) {
			return node1.succs;
		}

		return new ArrayList<N>(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#getOutEdges(T)
	 */
	public Collection<? extends IEdge<N, E>> getOutEdges(N node) {
		GraphNode<N, E> node1 = nodeMap.get(node);

		if (node1 != null) {
			return node1.outEdges;
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#getInEdges(T)
	 */
	public Collection<? extends IEdge<N, E>> getInEdges(N node) {
		GraphNode<N, E> node1 = nodeMap.get(node);

		if (node1 != null) {
			return node1.inEdges;
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fhg.iwu.commons.uima.internal.core.graph.IGraph#getPredecessors(T)
	 */
	public Collection<N> getPredecessors(N node) {
		GraphNode<N, E> node1 = nodeMap.get(node);

		if (node1 != null) {
			return node1.preds;
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.iwu.commons.uima.internal.core.graph.IGraph#getNodes()
	 */
	public Collection<N> getNodes() {
		return nodeMap.keySet();
	}

	@Override
	public boolean containsNode(N node) {
		return nodeMap.containsKey(node);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (GraphNode<N, E> node : nodeMap.values()) {
			for (Edge<N, E> edge : node.outEdges) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(edge);
			}
		}
		return sb.toString();
	}
}
