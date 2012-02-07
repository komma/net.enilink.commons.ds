package net.enilink.commons.ds.graph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ken Wenzel
 */
public class WeaklyConnectedComponentsVisitor<T, E> extends UndirectedDfsVisitor<T, E> {
    private List<IGraph<T, E>> graphs = null;
    private IGraph<T, E> newGraph;
    
    @Override
    public void start(IGraph<T, E> graph) {
        graphs = new ArrayList<IGraph<T, E>>();
        
        dfs(graph);
    }

    
    // preOrder
    @Override
    @SuppressWarnings({"unchecked"})
    protected void visitPre(T pred, E edge, T node) {
    	if (pred != null) {
	        newGraph.addNode(pred);
	        newGraph.addNode(node);
	        newGraph.addEdge(edge, pred, node);
    	}
    }

    // visits back edges
	@Override
	@SuppressWarnings({"unchecked"})
	protected void visitBack(T pred, E edge, T node) {
		newGraph.addNode(pred);
		newGraph.addEdge(edge, pred, node);
	}

	
	// visits forward and cross edges
	@Override
	@SuppressWarnings({"unchecked"})
	protected void visitOther(T pred, E edge, T node) {
		newGraph.addNode(pred);
		newGraph.addEdge(edge, pred, node);
	}
    
    @Override
    protected void startRoot(T node) {
        newGraph = new Graph<T, E>();
        newGraph.addNode(node);
    }
    
    @Override
    protected void finishRoot(T node) {
    	graphs.add(newGraph);
    	newGraph = null;
    }
    
    public List<IGraph<T, E>> getComponents() {
        return graphs;
    }
}
