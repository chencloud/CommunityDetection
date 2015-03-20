package edu.czy.measure;

import java.util.Collection;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;

public class MeasuerCollections {
	public static double calculateNMI(Collection<Collection<Integer>> partition,
			Collection<Collection<Integer>> partitionTrue,int nodeCount){
		return NMIMeasure.NMIPartition1(partition, partitionTrue, nodeCount);
	}
	public static double calculateQov(Collection<Collection<Vertex>> comms,SparseGraph<Vertex,Edge> graph) {
		return new ModularityOverlap(graph).modOverlap(comms);
	}
	public static double calculateQov(SparseGraph<Vertex,Edge> graph) {
		return Measure.computeOverlapModularityFor(graph);
	}
	public static double calculateQ(SparseGraph<Vertex,Edge> graph) {
		return  Measure.computeModularityFor(graph);
	}
	
	public static double calculateQForTruth(SparseGraph<Vertex,Edge> graph) {
		return Measure.computeModularityForTruth(graph);
	}
	public static double calculateQFromCollections(SparseGraph<Vertex,Edge> graph,Collection<Collection<Integer>> coms) {
		return 0.0;
	}
	public static double calculateQFromCollectionsForTruth(SparseGraph<Vertex,Edge> graph,Collection<Collection<Integer>> coms) {
		return 0.0;
	}
	
	public static double calculateQovFromCollections(SparseGraph<Vertex,Edge> graph,Collection<Collection<Integer>> coms) {
		return 0.0;
	}
}

