package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class SeedGroupBasedLPA {

	public static SparseGraph<Vertex,Edge> getSeedGroupBasedLPA(SparseGraph<Vertex,Edge> graph) {
		SparseGraph<Vertex,Edge> subgraph = new SparseGraph<Vertex,Edge>();
		Set<Vertex> localcenters = new HashSet<Vertex>();
		for(Vertex v:graph.getVertices()) {
			boolean islocalcenter = true;
			for(Vertex neigV:graph.getNeighbors(v)) {
				if(graph.degree(neigV)>graph.degree(v))
				{
					islocalcenter = false;
					break;
				}
			}
			if(islocalcenter) localcenters.add(v);
		}
		
		extractSubGraph(localcenters,graph,subgraph,3);
		return subgraph;
	}

	private static void extractSubGraph(Set<Vertex> localcenters,SparseGraph<Vertex, Edge> graph,
			SparseGraph<Vertex, Edge> subgraph, int i) {
		// TODO Auto-generated method stub
		if(i<=0)return;
		Set<Vertex> nextLayer = new HashSet<Vertex>();
		for(Vertex v:localcenters){
			if(!subgraph.containsVertex(v)){
				subgraph.addVertex(v);
			}
			for(Vertex neigh:graph.getNeighbors(v)) {
				nextLayer.add(neigh);
				if(!subgraph.containsVertex(neigh)){
					subgraph.addVertex(neigh);
				}
				if(subgraph.findEdge(v, neigh) == null){
					subgraph.addEdge(new Edge(v.getId(),neigh.getId()), v, neigh, EdgeType.UNDIRECTED); 
				}
			}
		}
		System.out.println(subgraph.getEdgeCount()+":"+subgraph.getVertexCount());
		localcenters.clear();
		localcenters.addAll(nextLayer);
		nextLayer.clear();
		i = i-1;
		extractSubGraph(localcenters,graph,subgraph,i);
	}
	public static void main(String[] args) {
//		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";
//		String filename="E:\\dataset\\unweight_dataset\\Karate\\Karate.gml";
//		String filename="E:\\dataset\\unweight_dataset\\dolphins\\dolphins.gml";
//		String filename="E:\\dataset\\unweight_dataset\\toy_network\\toy_network.net";
		String filename = "E:\\dataset\\unweight_dataset\\pro-pro\\pro-pro.net";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		
		SparseGraph<Vertex,Edge> subGraph = SeedGroupBasedLPA.getSeedGroupBasedLPA(graph);
		LPA standardlpa = new StandardLPA(subGraph,1000);
		System.out.println(subGraph.getEdgeCount()+":"+subGraph.getVertexCount());
		standardlpa.run();
		Collection<Collection<Vertex>> seedGroups = standardlpa.getCommunitysByVertex();
		GraphUtils.PrintCommunityCollectionsWithVertex(seedGroups, ";");
	}
}
