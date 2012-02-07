package net.enilink.commons.ds.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Ken Wenzel
 */
public class UndirectedDfsVisitor<T, E> extends DfsVisitor<T, E> {
    @Override
    protected Collection<? extends IEdge<T, E>> getOutEdges(IGraph<T, E> graph, T node) {
        Set<IEdge<T, E>> result = new HashSet<IEdge<T, E>>(graph.getOutEdges(node));
        result.addAll(graph.getInEdges(node));
        
        return result;
    }
}
