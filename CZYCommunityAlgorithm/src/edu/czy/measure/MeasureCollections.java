package edu.czy.measure;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;

public class MeasureCollections {
	public static double calculateNMI(Collection<Collection<Integer>> partition,
			Collection<Collection<Integer>> partitionTrue,int nodeCount){
		return NMIMeasure.NMIPartition1(partition, partitionTrue, nodeCount);
	}
	public static double calculateQovWithVertex(Collection<Collection<Vertex>> comms,SparseGraph<Vertex,Edge> graph) {
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
	public static double calculateQFromCollectionsWithVertex(SparseGraph<Vertex,Edge> graph,Collection<Collection<Vertex>> coms) {
		return modularity_Q(coms,graph);
	}
	public static double calculateQFromCollectionsWithInteger(SparseGraph<Vertex,Edge> graph,Collection<Collection<Integer>> coms,Map<Long,Vertex> nodeMap) {
		return modularity_Q(coms,graph,nodeMap);
	}
	public static double calculateQFromCollectionsForTruth(SparseGraph<Vertex,Edge> graph,Collection<Collection<Integer>> coms) {
		Map<Long,Vertex> nodeMap = new HashMap<Long,Vertex>();
		for(Vertex v:graph.getVertices()){
			nodeMap.put(v.getId(), v);
		}
		return modularity_Q(coms,graph,nodeMap);
	}
	
	public static double calculateQovFromCollectionsWithInteger(SparseGraph<Vertex,Edge> graph,Collection<Collection<Integer>> coms) {
		Map<Long,Vertex> nodeMap = new HashMap<Long,Vertex>();
		for(Vertex v:graph.getVertices()){
			nodeMap.put(v.getId(), v);
		}
		Collection<Collection<Vertex>> comVertexs = new ArrayList<Collection<Vertex>>();
		for(Collection<Integer> com :coms) {
			Collection<Vertex> newCom = new ArrayList<Vertex>();
			for(Integer id : com) {
				newCom.add(nodeMap.get(id));
			}
			comVertexs.add(newCom);
		}
        return new ModularityOverlap(graph).modOverlap(comVertexs);
	}
	
	// evaluation metric:modularity Q(<1)
	public static double modularity_Q(Collection<Collection<Vertex>> partition,SparseGraph<Vertex,Edge> graph) {
		double sum_Q = 0.0;
		double q = 0.0;
		for (Collection<Vertex> com : partition) {
			q = getPerCommunutyQ(com,graph);
			sum_Q += q;
		}
		return 0.5 * sum_Q / graph.getEdgeCount();
	}

	public static double getPerCommunutyQ(Collection<Vertex> com,SparseGraph<Vertex,Edge> graph) {
		double q = 0.0;
		int Aij = 0;
		int ki, kj;
		List<Vertex> vertices = new ArrayList<Vertex>(com);
		Collection<Vertex> neighborVertices;
		Vertex vi, vj;
		for (int i = 0; i < vertices.size() - 1; i++) {
			vi = vertices.get(i);
			neighborVertices = graph.getNeighbors(vi);
			ki = graph.degree(vi);
			for (int j = 0 ; j < vertices.size(); j++) {
				vj = vertices.get(j);
				if (neighborVertices.contains(vj)) {
					Aij = 1;
				} else {
					Aij = 0;
				}
				kj = graph.degree(vj);
				q += Aij - 0.5 * ki * kj / graph.getEdgeCount();
			}
		}
		return  q;
	}
	
	// evaluation metric:modularity Q(<1)
	public static double modularity_Q(Collection<Collection<Integer>> partition,SparseGraph<Vertex,Edge> graph,Map<Long,Vertex> nodeMap) {
		double sum_Q = 0.0;
		double q = 0.0;
		for (Collection<Integer> com : partition) {
			q = getPerCommunutyQ(com,graph,nodeMap);
			sum_Q += q;
		}
		return 0.5 * sum_Q / graph.getEdgeCount();
	}

	public static double getPerCommunutyQ(Collection<Integer> com,SparseGraph<Vertex,Edge> graph,Map<Long,Vertex> nodeMap) {
		double q = 0.0;
		int Aij = 0;
		int ki, kj;
		List<Vertex> vertices = new ArrayList<Vertex>();
		for(Integer id:com) {
			vertices.add(nodeMap.get((long)id));
		}
		Collection<Vertex> neighborVertices;
		Vertex vi, vj;
		for (int i = 0; i < vertices.size(); i++) {
			vi = vertices.get(i);
			neighborVertices = graph.getNeighbors(vi);
			ki = graph.degree(vi);
			for (int j = 0 ; j < vertices.size(); j++) {
				vj = vertices.get(j);
				if (neighborVertices.contains(vj)) {
					Aij = 1;
				} else {
					Aij = 0;
				}
				kj = graph.degree(vj);
				q += Aij - 0.5 * ki * kj / graph.getEdgeCount();
			}
		}
		return  q;
	}
}

