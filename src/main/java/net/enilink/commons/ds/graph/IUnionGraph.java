package net.enilink.commons.ds.graph;

import java.util.Collection;

public interface IUnionGraph<N, E> extends IGraph<N, E> {
	/**
     * <p>
     * Add the given graph to this composition.
     * </p>
     *
     * @param graph A sub-graph to add to this composition
     */
	boolean addGraph(IGraph<N, E> graph);

	/**
     * <p>
     * Answer a list of the graphs other than the updateable (base) graph
     * </p>
     *
     * @return A list of all of the sub-graphs, excluding the base graph.
     */
	Collection<IGraph<N, E>> getGraphs();

	/**
     * <p>
     * Remove the given graph from this composition.  If the removed graph is the
     * designated updateable graph, the updatable graph goes back to the default
     * for this composition.
     * </p>
     *
     * @param graph A sub-graph to remove from this composition
     */
	boolean removeGraph(IGraph<N, E> graph);

	/**
     * <p>
     * Answer the distinguished graph for the composition, which will be the graph
     * that receives triple adds and deletes. If no base graph is defined,
     * return null.
     * </p>
     *
     * @return The distinguished updateable graph, or null if there are no graphs
     *         in this composition
     */
	IGraph<N, E> getBaseGraph();

	 /**
     * <p>
     * Set the designated updateable graph for this composition.
     * </p>
     *
     * @param graph One of the graphs currently in this composition to be the
     *              designated graph to receive udpates
     * @exception IllegalArgumentException if graph is not one of the members of
     *             the composition
     */
	void setBaseGraph(IGraph<N, E> graph);

	 /**
     * <p>
     * Answer true if this graph contains the given graph as a sub-component.
     * </p>
     *
     * @param graph A graph to test
     * @return True if the graph is this graph, or is a sub-graph of this one.
     */
	boolean dependsOn(IGraph<N, E> graph);
}
