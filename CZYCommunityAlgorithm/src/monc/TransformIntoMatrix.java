package monc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;

public class TransformIntoMatrix {
	private SparseGraph<Vertex,Edge> graph;
	private Map<Integer,Vertex> nodeMap;
	TransformIntoMatrix(SparseGraph<Vertex,Edge> graph,Map<Integer,Vertex> nodeMap) {
		this.graph = graph;
		this.nodeMap = nodeMap;
	}

	public int[][] getAdjacentMatrix() {
		List<Integer> vIDList = new ArrayList<Integer>(nodeMap.keySet());
		Collections.sort(vIDList);
		int[][] adjacentMatrix = new int[vIDList.size()][vIDList.size()];
		Vertex v1, v2;
		for (int i = 0; i < vIDList.size(); i++) {
			v1 = getVertexById(vIDList.get(i));
			for (int j = 0; j < vIDList.size(); j++) {
				v2 = getVertexById(vIDList.get(j));
				if(graph.findEdge(v1, v2) != null || graph.findEdge(v2, v1) != null){
					adjacentMatrix[i][j] = 1;
				}
			}
		}
		return adjacentMatrix;
	}

	private Vertex getVertexById(Integer i) {
		// TODO Auto-generated method stub
		return this.nodeMap.get(i);
	}

}
