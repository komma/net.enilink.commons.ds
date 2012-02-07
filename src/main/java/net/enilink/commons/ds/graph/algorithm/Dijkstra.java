package net.enilink.commons.ds.graph.algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.enilink.commons.ds.graph.IEdge;
import net.enilink.commons.ds.graph.IGraph;
import net.enilink.commons.ds.misc.IMap;

/**
 * Dijkstra's algorithm adapted for the single-source many-targets shortest-path (SSMTSP) problem
 */
public class Dijkstra<N, E> {
	class NodeInfo {
		N node;
		double distance;
		NodeInfo pred;
	}

	protected IGraph<N, E> graph;
	protected Map<N, NodeInfo> nodeInfos;
	protected IMap<E, Double> weightFunction;
	protected FibonacciHeap<NodeInfo> fibHeap;

	public Dijkstra(IGraph<N, E> graph,
			final IMap<E, Double> weightFunction) {
		this.graph = graph;
		this.weightFunction = weightFunction;
		this.nodeInfos = new HashMap<N, NodeInfo>();
		this.fibHeap = new FibonacciHeap<NodeInfo>(new Comparator<NodeInfo>() {
			@Override
			public int compare(NodeInfo n1, NodeInfo n2) {
				if (n1.distance > n2.distance) {
					return 1;
				}
				return n1.distance < n2.distance ? -1 : 0;
			}
		});
	}

	protected void reset() {
		fibHeap.clear();
		nodeInfos.clear();
	}

	protected List<N> reconstructShortestPath(NodeInfo n) {
		LinkedList<N> path = new LinkedList<N>();
		path.addFirst(n.node);
		while (n.pred != null) {
			n = n.pred;
			path.addFirst(n.node);
		}
		return path;
	}

	public void run(N source) {
		run(source, null);
	}

	public void run(N source, Set<N> possibleTargets) {
		reset();

		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.node = source;
		nodeInfos.put(source, nodeInfo);

		fibHeap.insert(nodeInfo);
		while (!fibHeap.isEmpty()) {
			nodeInfo = fibHeap.removeMin();

			// break if node is free (node is not a possible target node)
			if (possibleTargets != null && !possibleTargets.contains(nodeInfo.node)) {
				return;
			}

			for (IEdge<N, E> edge : graph.getOutEdges(nodeInfo.node)) {
				double c = nodeInfo.distance
						+ weightFunction.map(edge.getData());
				N succNode = edge.getEnd();

				NodeInfo succInfo = nodeInfos.get(succNode);
				if (succInfo == null || c < succInfo.distance) {
					if (succInfo == null) {
						succInfo = new NodeInfo();
						succInfo.node = succNode;
						succInfo.distance = c;

						fibHeap.insert(succInfo);
					} else {
						succInfo.distance = c;

						fibHeap.decreaseKey(succInfo);
					}
					succInfo.pred = nodeInfo;
				}
			}

		}
	}
}
