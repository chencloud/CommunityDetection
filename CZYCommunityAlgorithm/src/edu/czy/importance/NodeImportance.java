package edu.czy.importance;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
public class NodeImportance {
	//public Map<Vertex,Double> scores;
	public static double getSmoothDegreeImportance(SparseGraph<Vertex,Edge> graph,Vertex v){
		double degree_importance = graph.degree(v)*1.0/(1.0+graph.degree(v));
		return degree_importance;
	}
	public static double getPageRankImportance(SparseGraph<Vertex,Edge> graph,Vertex v) {
		PageRank<Vertex, Edge> pr = new PageRank<Vertex, Edge>(graph,0.85);
		return pr.getVertexScore(v);
	}
}
