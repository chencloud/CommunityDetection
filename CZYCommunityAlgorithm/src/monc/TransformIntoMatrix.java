//package monc;
//
//import static util.UtilClass.getVertexById;
//import static util.UtilClass.graph;
//import static util.UtilClass.vidMap;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import datastructure.Vertex;
//
//public class TransformIntoMatrix {
//	TransformIntoMatrix() {
//	}
//
//	public int[][] getAdjacentMatrix() {
//		List<Integer> vIDList = new ArrayList<Integer>(vidMap.keySet());
//		Collections.sort(vIDList);
//		int[][] adjacentMatrix = new int[vIDList.size()][vIDList.size()];
//		Vertex v1, v2;
//		for (int i = 0; i < vIDList.size(); i++) {
//			v1 = getVertexById(vIDList.get(i));
//			for (int j = 0; j < vIDList.size(); j++) {
//				v2 = getVertexById(vIDList.get(j));
//				if(graph.findEdge(v1, v2) != null || graph.findEdge(v2, v1) != null){
//					adjacentMatrix[i][j] = 1;
//				}
//			}
//		}
//		return adjacentMatrix;
//	}
//
//}
