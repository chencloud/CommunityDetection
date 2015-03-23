package edu.czy.utils;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadEdgeFile;
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
					Number d = dist.getDistance(v1, v2);
					if(d!=null){
						totalPathLength += d.intValue();
						totalPathNum += 1;
					}
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
	public static int getMaxPathLength(SparseGraph<Vertex, Edge> graph) {
		// TODO Auto-generated method stub
		DijkstraDistance<Vertex, Edge> dist = new DijkstraDistance<Vertex, Edge>(graph);
		int maxLen = 0;
		for(Vertex v1:graph.getVertices()) {
			for(Vertex v2:graph.getVertices()) {
				if(v1.getId()<v2.getId()){
					Number d = dist.getDistance(v1, v2);
					if(d!=null){
						int len = dist.getDistance(v1, v2).intValue();
						maxLen = len>maxLen?len:maxLen;
					}

				}
			}
		}
		return maxLen;
	}
	public static double[] calculateAvgStd(double[] values) {
		double[] result = new double[2];
		double sumV = 0.0;
		for(int i=0;i<values.length;i++)
			sumV += values[i];
		result[0] = sumV/(1.0*values.length);
		sumV = 0;
		for(int i=0; i<values.length; i++) {
			sumV += (values[i]-result[0])*(values[i]-result[0]);
		}
		result[1] = Math.sqrt(sumV/(1.0*values.length));
		return result;
	}
	public static void main(String[] args){
//		String filename="E:\\dataset\\unweight_dataset\\karate\\karate.gml";
//		String filename="E:\\dataset\\unweight_dataset\\dolphins\\dolphins.gml";
//		String filename="E:\\dataset\\unweight_dataset\\Internet\\Internet.gml";
//		String filename="E:\\dataset\\unweight_dataset\\polbooks\\polbooks.gml";
//		String filename="E:\\dataset\\unweight_dataset\\jazz\\jazz.net";
//		String filename="E:\\dataset\\unweight_dataset\\email\\email.net";
//		String filename="E:\\dataset\\unweight_dataset\\mexican\\mexican.net";
//		String filename="E:\\dataset\\unweight_dataset\\pro-pro\\pro-pro.net";
//		String filename="E:\\dataset\\unweight_dataset\\celegans_metabolic\\celegans_metabolic.net";
//		String filename="E:\\dataset\\unweight_dataset\\pgp\\pgp.net";
//		String filename="E:\\dataset\\unweight_dataset\\power_grid\\power_grid.gml";
		
		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";

		SparseGraph<Vertex,Edge> graph = null;
		if(filename.endsWith(".gml")){
			LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
			graph=loadGML.loadGraph(filename);
		} else if(filename.endsWith(".net")) {
			LoadEdgeFile<Vertex,Edge> loadEdge = new LoadEdgeFile<Vertex,Edge>(Vertex.class,Edge.class);
			graph = loadEdge.loadGraph(filename);
		}
		GraphStatistic.printGraphStatistic(graph);
	}
}
