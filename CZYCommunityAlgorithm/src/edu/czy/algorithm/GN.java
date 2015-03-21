package edu.czy.algorithm;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.czy.lpa.LPA;
import edu.czy.lpa.LPAM;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
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
	public static void  main(String[] args) {
		String gmlfilename="J:\\paperproject\\DataSet\\karate\\karate.gml";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> graph=loadGML.loadGraph(gmlfilename);
		GN gn = new GN();
		Collection<Collection<Vertex>> coms = gn.GNMaxQ(graph);
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q=MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularrity Q="+Q);
	}
}
