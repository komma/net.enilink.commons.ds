package net.enilink.commons.ds.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class UnionGraph<N, E> implements IUnionGraph<N, E> {
	protected Collection<IGraph<N, E>> subGraphs = new LinkedHashSet<IGraph<N, E>>();
	protected IGraph<N, E> baseGraph;

	public UnionGraph() {
	}
	
	public UnionGraph(Collection<IGraph<N, E>> graphs) {
		for (IGraph<N, E> graph : graphs) {
			subGraphs.add(graph);
		}
	}

	@Override
	public boolean addGraph(IGraph<N, E> graph) {
		return subGraphs.add(graph);
	}

	@Override
	public Collection<IGraph<N, E>> getGraphs() {
		return Collections.unmodifiableCollection(subGraphs);
	}

	@Override
	public boolean removeGraph(IGraph<N, E> graph) {
		if (baseGraph == graph) {
			baseGraph = null;
		}

		return subGraphs.remove(graph);
	}

	@Override
	public boolean addEdge(E edge, N pred, N succ) {
		IGraph<N, E> baseGraph = getBaseGraph();
		if (baseGraph != null) {
			return baseGraph.addEdge(edge, pred, succ);
		}
		return false;
	}

	@Override
	public boolean addNode(N n) {
		IGraph<N, E> baseGraph = getBaseGraph();
		if (baseGraph != null) {
			return baseGraph.addNode(n);
		}
		return false;
	}

	@Override
	public Collection<? extends IEdge<N, E>> getInEdges(N node) {
		Set<IEdge<N, E>> inEdges = new LinkedHashSet<IEdge<N, E>>();
		for (IGraph<N, E> subGraph : subGraphs) {
			inEdges.addAll(subGraph.getInEdges(node));
		}
		return inEdges;
	}

	@Override
	public Collection<? extends N> getNodes() {
		Set<N> nodes = new HashSet<N>();
		for (IGraph<N, E> subGraph : subGraphs) {
			nodes.addAll(subGraph.getNodes());
		}
		return nodes;
	}

	@Override
	public Collection<? extends IEdge<N, E>> getOutEdges(N node) {
		Set<IEdge<N, E>> outEdges = new LinkedHashSet<IEdge<N, E>>();
		for (IGraph<N, E> subGraph : subGraphs) {
			outEdges.addAll(subGraph.getOutEdges(node));
		}
		return outEdges;
	}

	@Override
	public Collection<? extends N> getPredecessors(N node) {
		Set<N> preds = new HashSet<N>();
		for (IGraph<N, E> subGraph : subGraphs) {
			preds.addAll(subGraph.getPredecessors(node));
		}
		return preds;
	}

	@Override
	public Collection<? extends N> getSuccessors(N node) {
		Set<N> succs = new HashSet<N>();
		for (IGraph<N, E> subGraph : subGraphs) {
			succs.addAll(subGraph.getSuccessors(node));
		}
		return succs;
	}

	@Override
	public boolean containsEdge(E edge, N pred, N succ) {
		for (IGraph<N, E> subGraph : subGraphs) {
			if (subGraph.containsEdge(edge, pred, succ)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsNode(N node) {
		for (IGraph<N, E> subGraph : subGraphs) {
			if (subGraph.containsNode(node)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean dependsOn(IGraph<N, E> graph) {
		return graph == this || subGraphs.contains(graph);
	}

	@Override
	public IGraph<N, E> getBaseGraph() {
		return baseGraph;
	}

	@Override
	public void setBaseGraph(IGraph<N, E> graph) {
		if (subGraphs.contains(graph)) {
			baseGraph = graph;
		} else {
			throw new IllegalArgumentException(
					"The updateable graph must be one of the graphs from the composition");
		}
	}

}
