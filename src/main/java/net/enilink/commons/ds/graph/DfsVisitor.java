package net.enilink.commons.ds.graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ken Wenzel
 */
public class DfsVisitor<T, E> implements IGraphVisitor<T, E> {
    protected enum State { GRAY, BLACK };
    
    public void start(IGraph<T, E> graph) {
        dfs(graph);
    }
    
    protected void dfs(IGraph<T, E> graph) {
        Map<T, State> visited = new LinkedHashMap<T, State>(); 
        
        for (T root : getRoots(graph)) {
            if (visited.get(root) == null) {
                startRoot(root);
                dfsVisit(graph, null, null, root, visited);
                finishRoot(root);
            }
        }
    }
    
    protected Collection<T> getRoots(IGraph<T, E> graph) {
    	List<T> roots = new ArrayList<T>();
		for (T node : graph.getNodes()) {
			if (graph.getPredecessors(node).isEmpty()) {
				roots.add(node);
			}
		}
		return roots;
    }
    
    protected boolean dfsVisit(IGraph<T, E> graph, T pred, IEdge<T, E> edge, T node, Map<T, State> visited) {
        if (visited.get(node) == State.GRAY) {
        	visitBack(edge.getStart(), edge.getData(), edge.getEnd());
            return false;
        } else if (visited.get(node) == State.BLACK) {
            visitOther(edge.getStart(), edge.getData(), edge.getEnd());
            return false;
        }
        
        if (edge != null) {
        	visitPre(edge.getStart(), edge.getData(), edge.getEnd());
        } else {
        	visitPre(null, null, node);
        }

        visited.put(node, State.GRAY);
        
        for (IEdge<T, E> outEdge : getOutEdges(graph, node)) {            
            dfsVisit(graph, node, outEdge, (outEdge.getEnd() != node) ? outEdge.getEnd() : outEdge.getStart(), visited);
        }
        
        if (edge != null) {
        	visitPost(edge.getStart(), edge.getData(), edge.getEnd());
        } else {
        	visitPost(null, null, node);
        }
        
        visited.put(node, State.BLACK);
        return true;
    }
    
    protected Collection<? extends IEdge<T, E>> getOutEdges(IGraph<T, E> graph, T node) {
        return graph.getOutEdges(node);
    }
    
    /**
     * Called when DFS starts with a root node,
     * which was not visited yet
     */
    protected void startRoot(T node) {}
    
    /**
     * Called when DFS finishes a root node
     */
    protected void finishRoot(T node) {}

    protected void visitPre(T pred, E edge, T node) {}

    protected void visitPost(T pred, E edge, T node) {}

    protected void visitBack(T pred, E edge, T node) {}
    
    protected void visitOther(T pred, E edge, T node) {}
}
