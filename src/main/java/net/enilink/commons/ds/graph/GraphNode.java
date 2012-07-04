package net.enilink.commons.ds.graph;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Generic GraphNode implementation
 * 
 * @author Ken Wenzel
 */
class GraphNode<T, E> {
	protected Set<Edge<T, E>> inEdges = new LinkedHashSet<Edge<T, E>>();
	protected Set<Edge<T, E>> outEdges = new LinkedHashSet<Edge<T, E>>();

	protected Set<T> preds = new LinkedHashSet<T>(),
			succs = new LinkedHashSet<T>();
	protected T data;

	protected GraphNode(T data) {
		this.data = data;
	}

	boolean addOutEdge(E edge, GraphNode<T, E> succ) {
		Edge<T, E> edgeTriple = new Edge<T, E>(data, edge, succ.data);
		if (outEdges.add(edgeTriple)) {
			succs.add(succ.data);
			succ.preds.add(this.data);
			succ.inEdges.add(edgeTriple);
			return true;
		}
		return false;
	}

	boolean addInEdge(GraphNode<T, E> pred, E edge) {
		Edge<T, E> edgeTriple = new Edge<T, E>(pred.data, edge, data);
		if (inEdges.add(edgeTriple)) {
			preds.add(pred.data);
			pred.succs.add(this.data);
			pred.outEdges.add(edgeTriple);
			return true;
		}
		return false;
	}

	boolean removeOutEdge(E edge, GraphNode<T, E> succ) {
		IEdge<T, E> edgeTriple = new Edge<T, E>(data, edge, succ.data);
		if (outEdges.remove(edgeTriple)) {
			succs.remove(succ.data);
			succ.preds.remove(this.data);
			succ.inEdges.remove(edgeTriple);
			return true;
		}
		return false;
	}

	boolean removeInEdge(GraphNode<T, E> pred, E edge) {
		IEdge<T, E> edgeTriple = new Edge<T, E>(pred.data, edge, data);
		if (inEdges.remove(edgeTriple)) {
			preds.remove(pred.data);
			pred.succs.remove(this.data);
			pred.outEdges.remove(edgeTriple);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Edge<T, E>> edgeIt = outEdges.iterator();
		while (edgeIt.hasNext()) {
			sb.append(edgeIt.next());
			if (edgeIt.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
