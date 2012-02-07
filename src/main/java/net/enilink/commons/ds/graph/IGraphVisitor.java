package net.enilink.commons.ds.graph;

/**
 *
 * @author Ken Wenzel
 */
public interface IGraphVisitor<T, E> {
    void start(IGraph<T, E> graph);
}