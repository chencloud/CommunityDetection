package edu.czy.measure;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public class Measure {

	public static <V extends Vertex, E extends Edge>   double computeModularityFor(SparseGraph<V, E> graph){
		double Q = 0D;
		int innerEdgeCount = 0;
		int degreeOfNetwork = graph.getEdgeCount() * 2;
		Map<String, Integer> comDegreeMap = new HashMap<String, Integer>();
		Iterator<V> iter = graph.getVertices().iterator();
		Iterator<V> jter = null;
		StringBuffer sb = null;
		//delete homeless nodes
		int homelessNodeNum = 0;
		ArrayList<V> homelessNodes = new ArrayList<V>();
		while(iter.hasNext()){
			V node = iter.next();
			if(node.getValue().trim().equals("")){
				homelessNodes.add(node);
				if(sb == null){
					sb = new StringBuffer(Long.toString(node.getId()));
					continue;
				}else{
					sb.append("," + Long.toString(node.getId()));
				}
			}
		}
		homelessNodeNum = homelessNodes.size();
		for(V node: homelessNodes){
			graph.removeVertex(node);
		}
		homelessNodes.clear();
		iter = graph.getVertices().iterator();
		while(iter.hasNext()){
			V ver = iter.next();
			if(comDegreeMap.containsKey(ver.getValue().trim())){
				comDegreeMap.put(ver.getValue().trim(), comDegreeMap.get(ver.getValue()) + graph.degree(ver));
			}else{
				comDegreeMap.put(ver.getValue().trim(), graph.degree(ver));
			}
			jter = graph.getNeighbors(ver).iterator();
			while(jter.hasNext()){
				V neighbor = jter.next();
				if(neighbor.getValue().equals(ver.getValue())){
					innerEdgeCount++;
				}
			}
		}
		if(homelessNodeNum != 0){
			System.out.println("There are " + homelessNodeNum + " vertices belonging to no community:(node id) ");
			System.out.print(sb.toString());
			System.out.println();
		}
		Q += innerEdgeCount / (double)degreeOfNetwork;
		for(int ai: comDegreeMap.values()){
//			System.out.println("Q value is " + Q);
			double temp = ai / (double)degreeOfNetwork;
			Q -= temp * temp;
		}
		return Q;
	}
	
	public static <V extends Vertex, E extends Edge>   double computeModularityForTruth(SparseGraph<V, E> graph){
		double Q = 0D;
		int innerEdgeCount = 0;
		int degreeOfNetwork = graph.getEdgeCount() * 2;
		Map<String, Integer> comDegreeMap = new HashMap<String, Integer>();
		Iterator<V> iter = graph.getVertices().iterator();
		Iterator<V> jter = null;
		StringBuffer sb = null;
		//delete homeless nodes
		int homelessNodeNum = 0;
		ArrayList<V> homelessNodes = new ArrayList<V>();
		while(iter.hasNext()){
			V node = iter.next();
			if(node.getGroundTruth().trim().equals("")){
				homelessNodes.add(node);
				if(sb == null){
					sb = new StringBuffer(Long.toString(node.getId()));
					continue;
				}else{
					sb.append("," + Long.toString(node.getId()));
				}
			}
		}
		homelessNodeNum = homelessNodes.size();
		for(V node: homelessNodes){
			graph.removeVertex(node);
		}
		homelessNodes.clear();
		iter = graph.getVertices().iterator();
		while(iter.hasNext()){
			V ver = iter.next();
			if(comDegreeMap.containsKey(ver.getGroundTruth().trim())){
				comDegreeMap.put(ver.getGroundTruth().trim(), comDegreeMap.get(ver.getGroundTruth()) + graph.degree(ver));
			}else{
				comDegreeMap.put(ver.getGroundTruth().trim(), graph.degree(ver));
			}
			jter = graph.getNeighbors(ver).iterator();
			while(jter.hasNext()){
				V neighbor = jter.next();
				if(neighbor.getGroundTruth().equals(ver.getGroundTruth())){
					innerEdgeCount++;
				}
			}
		}
		if(homelessNodeNum != 0){
			System.out.println("There are " + homelessNodeNum + " vertices belonging to no community:(node id) ");
			System.out.print(sb.toString());
			System.out.println();
		}
		Q += innerEdgeCount / (double)degreeOfNetwork;
		for(int ai: comDegreeMap.values()){
//			System.out.println("Q value is " + Q);
			double temp = ai / (double)degreeOfNetwork;
			Q -= temp * temp;
		}
		return Q;
	}
	
	public static <V extends Vertex, E extends Edge> double computeOverlapModularityFor(SparseGraph<V, E> graph){
		double Q = 0D;
		double innerEdgeCount = 0D;
		Double ki = 0D;
		Map<String, Double> comDegreeMap = new HashMap<String, Double>();
		Iterator<V> iter = graph.getVertices().iterator();
		Iterator<V> jter = null;
		StringBuffer sb = null;
		//delete homeless nodes
		int homelessNodeNum = 0;
		ArrayList<V> homelessNodes = new ArrayList<V>();
		while(iter.hasNext()){
			V node = iter.next();
			if(node.getValue().trim().equals("")){
				homelessNodes.add(node);
				if(sb == null){
					sb = new StringBuffer(Long.toString(node.getId()));
					continue;
				}else{
					sb.append("," + Long.toString(node.getId()));
				}
			}
		}
		homelessNodeNum = homelessNodes.size();
		for(V node: homelessNodes){
			graph.removeVertex(node);
		}
		homelessNodes.clear();
		int degreeOfNetwork = graph.getEdgeCount() * 2;
		iter = graph.getVertices().iterator();
		while(iter.hasNext()){
			V ver = iter.next();
			String[] comsOfVer = ver.getValue().trim().split(GraphUtils.OverlapNode_Split);
			ki = graph.degree(ver) / (double)comsOfVer.length;
			for(String com: comsOfVer){
				if(comDegreeMap.containsKey(com.trim())){
					comDegreeMap.put(com.trim(), comDegreeMap.get(com) + ki);
				}else{
					comDegreeMap.put(com.trim(), ki);
				}
			}
			jter = graph.getNeighbors(ver).iterator();
			while(jter.hasNext()){
				V neighbor = jter.next();
				String[] comsOfNeighbor = neighbor.getValue().trim().split(GraphUtils.OverlapNode_Split);
				for(String com1: comsOfVer){
					for(String com2: comsOfNeighbor){
						if(com2.equals(com1)){
							innerEdgeCount += 1.0 / (comsOfNeighbor.length * comsOfVer.length);
							break;
						}
					}
				}
			}
		}
//		if(homelessNodeNum != 0){
//			System.out.println("There are " + homelessNodeNum + " vertices belonging to no community:(node id) ");
//			System.out.print(sb.toString());
//			System.out.println();
//		}
		Q += innerEdgeCount / degreeOfNetwork;
		for(double ai: comDegreeMap.values()){
//			System.out.println("Q value is " + Q);
			double temp = ai / degreeOfNetwork;
			Q -= temp * temp;
//			Q -= ai * ai / (degreeOfNetwork * degreeOfNetwork);
		}
		return Q;
	}
	
	/*private static <V extends Vertex, E extends Edge> Map<String, Double> computeBelongness(SparseGraph<V, E> graph, V v){
		Iterator<V> neigh = graph.getNeighbors(v).iterator();
		Map<String, Double> belongness = new HashMap<String, Double>();
		double weight = 0D;
		while(neigh.hasNext()){
			V u = neigh.next();
			E e = graph.findEdge(u, v);
			weight += e.getWeight();
			if(u.getValue().contains(",")){
				String[] items = u.getValue().split(",");
				for(String item: items){
					if(belongness.containsKey(item)){
						belongness.put(item, belongness.get(item) + e.getWeight() / items.length);
					}else{
						belongness.put(item, e.getWeight() / items.length);
					}
				}
			}else{
				if(belongness.containsKey(u.getValue())){
					belongness.put(u.getValue(), belongness.get(u.getValue()) + e.getWeight());
				}else{
					belongness.put(u.getValue(), e.getWeight());
				}
			}
		}
		for(String label: belongness.keySet()){
			belongness.put(label, belongness.get(label) / weight);
		}
		return belongness;
	}*/
	
//	public static <V extends Vertex, E extends Edge> double computeWeightedOverlapModularityFor(SparseGraph<V, E> graph){
//		/*double Qov = 0D;
//		Map<Long, Map<String, Double>> nodeBelongness = new HashMap<Long, Map<String, Double>>();
//		Map<Long, Double> nodeWeight = new HashMap<Long, Double>();
//		double W = 0D;
//		Iterator<V> vter = graph.getVertices().iterator();
//		while(vter.hasNext()){
//			V u = vter.next();
//			double wei = 0D;
//			Map<String, Double> nodeBelong = new HashMap<String, Double>();
//			Iterator<V> nter = graph.getNeighbors(u).iterator();
//			while(nter.hasNext()){
//				V v = nter.next();
//				String value = v.getValue();
//				E e = graph.findEdge(u, v);
//				wei += e.getWeight();
//				W += e.getWeight();
//				Set<String> vSet = new HashSet<String>();
//				for(String val: value.split(","))
//					vSet.add(val);
//				for(String val: vSet){
//					if(nodeBelong.containsKey(val)){
//						nodeBelong.put(val, nodeBelong.get(val) + e.getWeight() / vSet.size()); 
//					}else{
//						nodeBelong.put(val, e.getWeight() / vSet.size());
//					}
//				}
//			}
//			nodeWeight.put(u.getId(), wei);
//			nodeBelongness.put(u.getId(), nodeBelong);
//		}
//		vter = graph.getVertices().iterator();
//		while(vter.hasNext()){
//			V u = vter.next();
//			String valu = u.getValue();
//			Iterator<V> nter = graph.getNeighbors(u).iterator();
//			Set<String> uSet = new HashSet<String>();
//			for(String ul: valu.split(","))
//				uSet.add(ul);
//			while(nter.hasNext()){
//				V v = nter.next();
//				String valv = v.getValue();
//				Set<String> vSet = new HashSet<String>();
//				for(String v1: valv.split(","))
//					vSet.add(v1);
//				uSet.retainAll(vSet);
//				if(uSet.isEmpty())
//					continue;
//				E euv = graph.findEdge(u, v);
//				double wuv = euv.getWeight();
//				double delta = (wuv / W) - (nodeWeight.get(u.getId()) / W) * (nodeWeight.get(v.getId()) / W);
//				for(String com: uSet){
//					double kcu = nodeBelongness.get(u.getId()).get(com) / nodeWeight.get(u.getId());
//					double kcv = nodeBelongness.get(v.getId()).get(com) / nodeWeight.get(v.getId());
//					Qov += kcu * kcv * delta;
//				}
//			}
//		}
////		System.out.println("The Qov is " + Qov);
//		return Qov;*/
//		double Q = 0D;
//		double innerWeightCount = 0D;
//		Double wi = 0D;
//		Map<String, Double> comWeightMap = new HashMap<String, Double>();
//		Iterator<V> iter = graph.getVertices().iterator();
//		Iterator<V> jter = null;
//		StringBuffer sb = null;
//		//delete homeless nodes
//		int homelessNodeNum = 0;
//		ArrayList<V> homelessNodes = new ArrayList<V>();
//		while(iter.hasNext()){
//			V node = iter.next();
//			if(node.getValue().trim().equals("")){
//				homelessNodes.add(node);
//				if(sb == null){
//					sb = new StringBuffer(Long.toString(node.getId()));
//					continue;
//				}else{
//					sb.append("," + Long.toString(node.getId()));
//				}
//			}
//		}
//		homelessNodeNum = homelessNodes.size();
//		for(V node: homelessNodes){
//			graph.removeVertex(node);
//		}
//		homelessNodes.clear();
//		double weightOfNetwork = CDATools.getWeightOf(graph) * 2;
//		iter = graph.getVertices().iterator();
//		while(iter.hasNext()){
//			V ver = iter.next();
//			String[] comsOfVer = ver.getValue().trim().split(",");
//			wi = CDATools.getAllWeightsOf(ver, graph) / comsOfVer.length;
//			for(String com: comsOfVer){
//				if(comWeightMap.containsKey(com.trim())){
//					comWeightMap.put(com.trim(), comWeightMap.get(com) + wi);
//				}else{
//					comWeightMap.put(com.trim(), wi);
//				}
//			}
//			jter = graph.getNeighbors(ver).iterator();
//			while(jter.hasNext()){
//				V neighbor = jter.next();
//				String[] comsOfNeighbor = neighbor.getValue().trim().split(",");
//				for(String com1: comsOfVer){
//					for(String com2: comsOfNeighbor){
//						if(com2.equals(com1)){
//							E edge = graph.findEdge(ver, neighbor);
//							innerWeightCount += edge.getWeight() / (comsOfNeighbor.length * comsOfVer.length);
//							break;
//						}
//					}
//				}
//			}
//		}
////		if(homelessNodeNum != 0){
////			System.out.println("There are " + homelessNodeNum + " vertices belonging to no community:(node id) ");
////			System.out.print(sb.toString());
////			System.out.println();
////		}
//		Q += innerWeightCount / weightOfNetwork;
//		for(double ai: comWeightMap.values()){
////			System.out.println("Q value is " + Q);
//			double temp = ai / weightOfNetwork;
//			Q -= temp * temp;
////			Q -= ai * ai / (degreeOfNetwork * degreeOfNetwork);
//		}
//		return Q;
//	}
	
}

