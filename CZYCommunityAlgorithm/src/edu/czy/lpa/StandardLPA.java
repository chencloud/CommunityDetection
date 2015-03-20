package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;

/**
 * 
 * @author CHENZHIYUN
 *
 *Standard Label 
 */
public class StandardLPA extends LPA{
	
	private int iteration;
	public StandardLPA(SparseGraph<Vertex,Edge> g)
	{
		this(g,20);
	}
	public StandardLPA(SparseGraph<Vertex,Edge> g,int iteration)
	{
		super(g);
		this.iteration=iteration;
	}
	
	public void run(){
		//Step one
		//init
		initNodeLabel();
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
			Map<String,Integer> hashmap=new HashMap<String,Integer>();
			List<String> arrayLabels=new ArrayList<String>();
			int maxCount=0;
			boolean isUpdated=false;
			for(int i=0;i<nodes.length;i++)
			{
				Vertex curV=nodes[i];
				Collection<Vertex> Vneighbors=this.graph.getNeighbors(curV);
				maxCount=0;
				hashmap.clear();
				arrayLabels.clear();
				for(Vertex v:Vneighbors)
				{
					String Vlabel=v.getValue();
					if(!hashmap.containsKey(Vlabel))
						hashmap.put(Vlabel, 1);
					else
					{
						int count=hashmap.get(Vlabel)+1;
						hashmap.put(Vlabel, count);
					}
					if(maxCount<hashmap.get(Vlabel))
					{
						maxCount=hashmap.get(Vlabel);
					}
				}
				for(String label:hashmap.keySet())
				{
					if(hashmap.get(label)==maxCount)
						arrayLabels.add(label);
				}
				//random choose one
				int rIndex=rand.nextInt(arrayLabels.size());
				if(!curV.getValue().trim().equals(arrayLabels.get(rIndex).trim())){
						curV.setValue(arrayLabels.get(rIndex));
						isUpdated=true;
				}
	
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
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}
