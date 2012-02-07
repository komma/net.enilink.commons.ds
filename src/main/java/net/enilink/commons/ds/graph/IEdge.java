package net.enilink.commons.ds.graph;

public interface IEdge<N, E> {
	N getStart();

	N getEnd();

	E getData();
}