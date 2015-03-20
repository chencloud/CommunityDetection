package edu.czy.algorithm;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.measure.MeasureCollections;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.SparseGraph;

public class GN {
	public Collection<Collection<Vertex>> GNMaxQ(SparseGraph<Vertex,Edge> graph) {
		Collection<Collection<Vertex>> results = null;
		double max_q = 0.0;
		for(int count=graph.getVertexCount()-1;count>1;count-- ) {
			EdgeBetweennessClusterer<Vertex, Edge> part = new EdgeBetweennessClusterer<Vertex, Edge>(
					count);
			Set<Set<Vertex>> p1 = part.transform(graph);
			Collection<Collection<Vertex>> p2 = new HashSet<Collection<Vertex>>();
			for(Set<Vertex> st : p1){
				Collection<Vertex> colt = new HashSet<Vertex>(st);
				p2.add(colt);
			}
			double q = MeasureCollections.calculateQFromCollectionsWithVertex(graph, p2);
			if(q > max_q) {
				max_q = q;
				results = p2;
			}
		}
		return results;
	}
}
