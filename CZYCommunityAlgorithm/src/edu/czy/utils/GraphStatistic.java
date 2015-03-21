package edu.czy.utils;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
public class GraphStatistic {
	public static double calculateAvgDegree(SparseGraph<Vertex,Edge> graph) {
		int totalDegree = graph.getEdgeCount()*2;
		return totalDegree/(graph.getVertexCount()*1.0);
	}
	public static double calculateAvgPathLength(SparseGraph<Vertex,Edge> graph) {
		DijkstraDistance<Vertex, Edge> dist = new DijkstraDistance<Vertex, Edge>(graph);
		int totalPathLength = 0;
		int totalPathNum = 0;
		for(Vertex v1:graph.getVertices()) {
			for(Vertex v2:graph.getVertices()) {
				if(v1.getId()<v2.getId()){
					totalPathLength += dist.getDistance(v1, v2).intValue();
					totalPathNum += 1;
				}
			}
		}
		return totalPathLength*1.0/(1.0*totalPathNum);
	}
	public static int getNodeCount(SparseGraph<Vertex,Edge> graph){
		return graph.getVertexCount();
	}
	public static int getEdgeCount(SparseGraph<Vertex,Edge> graph){
		return graph.getEdgeCount();
	}
	public static void printGraphStatistic(SparseGraph<Vertex,Edge> graph){
		System.out.println("N="+getNodeCount(graph));
		System.out.println("E="+getEdgeCount(graph));
		System.out.println("AvgDegree="+calculateAvgDegree(graph));
		System.out.println("AvgPathLength="+calculateAvgPathLength(graph));
		System.out.println("MaxPathLength="+getMaxPathLength(graph));
	}
	private static int getMaxPathLength(SparseGraph<Vertex, Edge> graph) {
		// TODO Auto-generated method stub
		DijkstraDistance<Vertex, Edge> dist = new DijkstraDistance<Vertex, Edge>(graph);
		int maxLen = 0;
		for(Vertex v1:graph.getVertices()) {
			for(Vertex v2:graph.getVertices()) {
				if(v1.getId()<v2.getId()){
					int len = dist.getDistance(v1, v2).intValue();
					maxLen = len>maxLen?len:maxLen;
				}
			}
		}
		return maxLen;
	}
	public static void main(String[] args){
//		String gmlfilename="E:\\dataset\\unweight_dataset\\karate\\karate.gml";
//		String gmlfilename="E:\\dataset\\unweight_dataset\\dolphins\\dolphins.gml";
		String gmlfilename="E:\\dataset\\unweight_dataset\\Internet\\Internet.gml";
		String edgefilename="";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> graph=loadGML.loadGraph(gmlfilename);
//		LoadEdgeFile<Vertex,Edge> loadEdge = new LoadEdgeFile<Vertex,Edge>(Vertex.class,Edge.class);
//		SparseGraph<Vertex,Edge> sparse_graph = loadEdge.loadGraph(edgefilenam);
		GraphStatistic.printGraphStatistic(graph);
	}
}
