package edu.czy.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;

public class GraphUtils {
	public final static double minIntersectionRatio = 0.5;
	public final static String  OverlapNode_Split = ",";
	public final static double eps = 2.2204e-16;
	public final static double coefficient = 0.8;
	public final static double minCommunity = 3;
	public static Double calcalueEducianSimilarity(Vertex curV, Vertex v) {
		// TODO Auto-generated method stub
		
		double simvalue=0.0;
		int len=curV.getWeight().size();
		double curV_dist = 0.0;
		double v_dist = 0;
		for(int i=0;i<len;i++)
		{
			simvalue+=Math.pow(curV.getWeight().get(i)-v.getWeight().get(i),2);
		}
		return 1/(simvalue+1.0);
	}
	public static Double calcalueCosineSimilarity(Vertex curV, Vertex v) {
		// TODO Auto-generated method stub
		
		double simvalue=0.0;
		int len=curV.getWeight().size();
		double curV_dist = 0.0;
		double v_dist = 0;
		for(int i=0;i<len;i++)
		{
			//simvalue+=Math.pow(curV.getWeight().get(i)-v.getWeight().get(i),2);
			simvalue += curV.getWeight().get(i)*v.getWeight().get(i);
			curV_dist += Math.pow(curV.getWeight().get(i), 2);
			v_dist += Math.pow(v.getWeight().get(i), 2);
		}
		return (0.5+0.5*simvalue/(Math.sqrt(curV_dist)*Math.sqrt(v_dist)));
	}
//	public static Double calcalueAdjustCosineSimilarity(Vertex curV, Vertex v) {
//		// TODO Auto-generated method stub
//		
//		double simvalue=0.0;
//		int len=curV.getWeight().size();
//		double curV_dist = 0.0;
//		double v_dist = 0;
//		double curV_avg = 0.0;double v_avg = 0.0;
//		for(int i=0;i<len;i++) {
//			curV_avg += curV.getWeight().get(i);
//			v_avg += curV.getWeight().get(i);
//		}
//		curV_avg /= 1.0*len;
//		v_avg /= 1.0*len;
//		for(int i=0;i<len;i++)
//		{
//			//simvalue+=Math.pow(curV.getWeight().get(i)-v.getWeight().get(i),2);
//			simvalue += (curV.getWeight().get(i)-curV_avg)*(v.getWeight().get(i)-v_avg);
//			curV_dist += Math.pow((curV.getWeight().get(i)-curV_avg), 2);
//			v_dist += Math.pow((v.getWeight().get(i)-v_avg), 2);
//		}
//		return simvalue/(Math.sqrt(curV_dist)*Math.sqrt(v_dist));
//	}
	public static Double calcaluePearsonSimilarity(Vertex curV, Vertex v) {
		// TODO Auto-generated method stub
		
		double simvalue=0.0;
		int len=curV.getWeight().size();
		double curV_dist = 0.0;
		double v_dist = 0;
		double curV_avg = 0.0;double v_avg = 0.0;
		for(int i=0;i<len;i++) {
			curV_avg += curV.getWeight().get(i);
			v_avg += v.getWeight().get(i);
		}
		curV_avg /= 1.0*len;
		v_avg /= 1.0*len;
		for(int i=0;i<len;i++)
		{
			//simvalue+=Math.pow(curV.getWeight().get(i)-v.getWeight().get(i),2);
			simvalue += (curV.getWeight().get(i)-curV_avg)*(v.getWeight().get(i)-v_avg);
			//if(simvalue != 0.0) 
			{
				curV_dist += Math.pow((curV.getWeight().get(i)-curV_avg), 2);
				
				v_dist += Math.pow((v.getWeight().get(i)-v_avg), 2);
				
			}
		}
		System.out.println(simvalue);
		System.out.println(v_dist);
		System.out.println(curV_dist);
		return (0.5+0.5*simvalue/(Math.sqrt(curV_dist)*Math.sqrt(v_dist)));
	}
	public static Double calcaluePearsonSimilarity(Collection<Double> v1, Collection<Double> v2) {
		// TODO Auto-generated method stub
		
		double simvalue=0.0;
		int len=v1.size();
		double curV_dist = 0.0;
		double v_dist = 0;
		double curV_avg = 0.0;double v_avg = 0.0;
		Double[] array_v1 = v1.toArray(new Double[0]);
		Double[] array_v2 = v2.toArray(new Double[0]);
		for(int i=0;i<len;i++) {
			curV_avg +=array_v1[i];
			v_avg +=array_v2[i];
		}
		curV_avg /= 1.0*len;
		v_avg /= 1.0*len;
		for(int i=0;i<len;i++)
		{
			//simvalue+=Math.pow(curV.getWeight().get(i)-v.getWeight().get(i),2);
			simvalue += (array_v1[i]-curV_avg)*(array_v2[i]-v_avg);
			//if(simvalue != 0.0) 
			{
				curV_dist += Math.pow((array_v1[i]-curV_avg), 2);
				
				v_dist += Math.pow((array_v2[i]-v_avg), 2);
				
			}
		}
		return simvalue/(Math.sqrt(curV_dist)*Math.sqrt(v_dist));
	}
	public static Collection<Collection<Integer>> exportCommunityCollection(SparseGraph<Vertex,Edge> graph) {
		Map<String,Collection<Integer>> results = new HashMap<String,Collection<Integer>>();
		for(Vertex v:graph.getVertices()) {
			String values = v.getValue();
			if(values.split(GraphUtils.OverlapNode_Split).length>1) {
				for(String value:values.split(GraphUtils.OverlapNode_Split)) {
					if(!results.containsKey(value)) {
						results.put(value, new ArrayList<Integer>());
					}
					results.get(value).add((int)v.getId());
				}
			} else {
				if(!results.containsKey(values)) {
					results.put(values, new ArrayList<Integer>());
				}
				results.get(values).add((int)v.getId());
			}
		}
		
		return results.values();
		
	}
	public static Collection<Collection<Vertex>> exportCommunityCollectionWithVertex(SparseGraph<Vertex,Edge> graph) {
		Map<String,Collection<Vertex>> results = new HashMap<String,Collection<Vertex>>();
		for(Vertex v:graph.getVertices()) {
			String values = v.getValue();
			if(values.split(GraphUtils.OverlapNode_Split).length>1) {
				for(String value:values.split(GraphUtils.OverlapNode_Split)) {
					if(!results.containsKey(value)) {
						results.put(value, new ArrayList<Vertex>());
					}
					results.get(value).add(v);
				}
			} else {
				if(!results.containsKey(values)) {
					results.put(values, new ArrayList<Vertex>());
				}
				results.get(values).add(v);
			}
		}
		
		return results.values();
		
	}
	public static Collection<Collection<Integer>> exportCommunityGroundTruthCollection(SparseGraph<Vertex,Edge> graph) {
		Map<String,Collection<Integer>> results = new HashMap<String,Collection<Integer>>();
		for(Vertex v:graph.getVertices()) {
			String values = v.getGroundTruth();
			if(values.split(GraphUtils.OverlapNode_Split).length>1) {
				for(String value:values.split(GraphUtils.OverlapNode_Split)) {
					if(!results.containsKey(value)) {
						results.put(value, new ArrayList<Integer>());
					}
					results.get(value).add((int)v.getId());
				}
			} else {
				if(!results.containsKey(values)) {
					results.put(values, new ArrayList<Integer>());
				}
				results.get(values).add((int)v.getId());
			}
		}
		
		return results.values();
		
	}
	public static Collection<Collection<Vertex>> exportCommunityGroundTruthCollectionVertex(SparseGraph<Vertex,Edge> graph) {
		Map<String,Collection<Vertex>> results = new HashMap<String,Collection<Vertex>>();
		for(Vertex v:graph.getVertices()) {
			String values = v.getGroundTruth();
			if(values.split(GraphUtils.OverlapNode_Split).length>1) {
				for(String value:values.split(GraphUtils.OverlapNode_Split)) {
					if(!results.containsKey(value)) {
						results.put(value, new ArrayList<Vertex>());
					}
					results.get(value).add(v);
				}
			} else {
				if(!results.containsKey(values)) {
					results.put(values, new ArrayList<Vertex>());
				}
				results.get(values).add(v);
			}
		}
		
		return results.values();
		
	}
	public static Collection<Entry<String,Collection<Integer>>> exportCommunityEntrySet(SparseGraph<Vertex,Edge> graph) {
		Map<String,Collection<Integer>> results = new HashMap<String,Collection<Integer>>();
		for(Vertex v:graph.getVertices()) {
			String values = v.getValue();
			if(values.split(GraphUtils.OverlapNode_Split).length>1) {
				for(String value:values.split(GraphUtils.OverlapNode_Split)) {
					if(!results.containsKey(values)) {
						results.put(values, new ArrayList<Integer>());
					}
					results.get(values).add((int)v.getId());
				}
			} else {
				if(!results.containsKey(values)) {
					results.put(values, new ArrayList<Integer>());
				}
				results.get(values).add((int)v.getId());
			}
		}
		
		return results.entrySet();
	}
	public static void PrintCommunityCollections(Collection<Collection<Integer>> coms ,String delim) {
		for(Collection<Integer> com :coms) {
			for(Integer id:com){
				System.out.print(id);
				System.out.print(delim);
			}
			System.out.println();
		}
	}
	public static void PrintCommunityCollectionsWithVertex(
			Collection<Collection<Vertex>> coms, String delim) {
		// TODO Auto-generated method stub
		for(Collection<Vertex> com :coms) {
			for(Vertex id:com){
				System.out.print(id.getId());
				System.out.print(delim);
			}
			System.out.println();
		}
	}
}
