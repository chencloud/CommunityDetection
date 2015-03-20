package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.importance.NodeImportance;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public class NMFWLPA {
	private SparseGraph<Vertex,Edge> graph;
	private int iteration;
	private static final double EPSION=0.00000000001;
	public NMFWLPA(SparseGraph<Vertex,Edge> g)
	{
		this(g,20);
	}
	public NMFWLPA(SparseGraph<Vertex,Edge> g,int iteration)
	{
		this.graph=g;
		this.iteration=iteration;
	}
	
	public void run(){
		//Step one
		//init
		initNodeLabel();
		//Step Two
		int itera=1;
		while(itera<=this.iteration){
			System.out.println("the current iteration:"+itera);
			//Step two
			//random the order of all nodes in graph
			Vertex[] nodes=this.graph.getVertices().toArray(new Vertex[0]);
			int nodecount=nodes.length;
			Random rand=new Random();
			//shuffle card
			for(int i=--nodecount;i>=0;i--)
			{
				int index=rand.nextInt(i+1);
				Vertex tempV=nodes[index];
				nodes[index]=nodes[i];
				nodes[i]=tempV;
			}
			
			//Step three
			//Update the label of each vertex according to its neigbors' label
			Map<String,Double> hashmap=new HashMap<String,Double>();
			List<String> arrayLabels=new ArrayList<String>();
			double maxValue=0;
			boolean isUpdated=false;
			for(int i=0;i<nodes.length;i++)
			{
				Vertex curV=nodes[i];
				Collection<Vertex> Vneighbors=this.graph.getNeighbors(curV);
				maxValue=0.0;
				hashmap.clear();
				arrayLabels.clear();
				for(Vertex v:Vneighbors)
				{
					String Vlabel=v.getValue();
					if(!hashmap.containsKey(Vlabel)){
						hashmap.put(Vlabel, 0.0);
					}
					{	
						double node_importance = NodeImportance.getSmoothDegreeImportance(graph, v);
//						double node_importance = NodeImportance.getPageRankImportance(graph, v);
						double community_blong = 1.0;
						for(Vertex vv : graph.getNeighbors(v)) {
							if(vv.getValue().trim().equals(v.getValue().trim())){
								community_blong += 1;
							}
						}
						community_blong = (community_blong+1)/(1.0+this.graph.degree(v));
						double count=hashmap.get(Vlabel)+Math.sqrt(GraphUtils.calcalueEducianSimilarity(curV, v)*node_importance)*community_blong;
						hashmap.put(Vlabel, count);
//						System.out.println(Vlabel + ":" + count);
					}
					if(hashmap.get(Vlabel)-maxValue>0.0)
					{
						maxValue=hashmap.get(Vlabel);
					}
				}
				for(String label:hashmap.keySet())
				{
					if(Math.abs(hashmap.get(label)-maxValue)<=EPSION)
						arrayLabels.add(label);
				}
				//random choose one
				if(arrayLabels.size()>1){
				System.out.println("Vertex "+curV.getId()+" multiLabel Occur");
			}
				int rIndex=rand.nextInt(arrayLabels.size());
				if(!curV.getValue().trim().equals(arrayLabels.get(rIndex).trim())){
						curV.setValue(arrayLabels.get(rIndex));
						isUpdated=true;
				}
				//random choose one
				//System.out.println(arrayLabels.size());
//				if(arrayLabels.size()>1){
//					System.out.println("Vertex "+curV.getId()+" multiLabel Occur");
//					for(Vertex v:arrayLabels){
//						System.out.print(v.getId()+";");
//					}
//					System.out.println();
//					ArrayList<Vertex> max_V = new ArrayList<Vertex>();
//					double max_Value = 0.0;
//					boolean isExistedOriginLabel = false;
//					for(Vertex v:arrayLabels){
//						if(v.getValue().trim().equals(curV.getValue().trim())){
//							isExistedOriginLabel = true;
//							break;
//						}
//						double node_importance = NodeImportance.getSmoothDegreeImportance(graph, v);
//						double interest_sim_importance = Math.sqrt(GraphUtils.calcalueEducianSimilarity(curV, v)*node_importance);
//						if(max_Value<interest_sim_importance){
//							max_Value = interest_sim_importance;
//							max_V.clear();max_V.add(v);
//						} else if(Math.abs(interest_sim_importance-maxValue)<=EPSION){
//							max_V.add(v);
//						}
//					}
//					if(!isExistedOriginLabel && max_V.size() >0){
//						if(max_V.size()>1)System.out.println("sub multilabel occure");
//						curV.setValue(max_V.get(0).getValue());
//						isUpdated=true;
//					}
//				} else {
//					curV.setValue(arrayLabels.get(0).getValue());
//					isUpdated=true;
//				}

	
			}
			if(!isUpdated)
			{
				break;
			}
			itera++;
			
		}
	}
	
	private void initNodeLabel() {
		// TODO Auto-generated method stub
		for(Vertex v:this.graph.getVertices()) {
			v.setValue(String.valueOf(v.getId()));
		}
	}

}
