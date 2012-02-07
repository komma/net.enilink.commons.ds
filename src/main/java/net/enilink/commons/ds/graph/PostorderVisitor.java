package net.enilink.commons.ds.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class PostorderVisitor<T, E> extends DfsVisitor<T, E> {
	private Set<T> visited;
	private boolean valid;
	
	@Override
	public void start(IGraph<T, E> graph) {
		visited = new LinkedHashSet<T>();
		valid = true;
		super.start(graph);
	}
	
	
	@Override
	protected void visitPost(T pred, E edge, T node) {
		visited.add(node);
	}


	@Override
	protected void visitBack(T pred, E edge, T node) {
		valid = false;
	}

	@Override
	protected void visitOther(T pred, E edge, T node) {
		valid = false;
	}

	public Collection<T> getOrder() {
		return visited;
	}

	public boolean isValid() {
		return valid;
	}
}
