package net.enilink.commons.ds.graph;

import java.util.Collection;

public interface IGraph<N, E> {
	boolean addNode(N n);

	boolean addEdge(E edge, N pred, N succ);

	boolean containsEdge(E edge, N pred, N succ);
	
	boolean containsNode(N node);
	
	Collection<? extends N> getSuccessors(N node);

	Collection<? extends IEdge<N, E>> getOutEdges(N node);

	Collection<? extends IEdge<N, E>> getInEdges(N node);

	Collection<? extends N> getPredecessors(N node);

	Collection<? extends N> getNodes();
}