package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

public abstract class LPA {
	protected SparseGraph<Vertex,Edge> graph;
	protected Map<Long,Vertex> nodeMap;
	protected int iteration;
	public LPA(SparseGraph<Vertex,Edge> g,int itera,boolean initNodeMap) {
		this.iteration = itera;
		this.graph = g;
		if(initNodeMap) {
			this.nodeMap = new HashMap<Long,Vertex>();
			for(Vertex v:g.getVertices()){
				nodeMap.put(v.getId(), v);
			}
		}
	}
	public abstract void init();
	public void run(){
		int itera=1;
		while(itera<=this.iteration){
			//random the order of all nodes in graph
			Vertex[] nodes=this.graph.getVertices().toArray(new Vertex[0]);
			int nodecount=nodes.length;
			//shuffle card
			for(int i=--nodecount;i>=0;i--)
			{
				int index=RandomNumGenerator.getRandomInt(i+1);
				Vertex tempV=nodes[index];
				nodes[index]=nodes[i];
				nodes[i]=tempV;
			}
			if(!updatelabel(nodes))
				break;
			itera++;
			
		}
	}
	public abstract boolean updatelabel(Vertex[] v);
	public Collection<Collection<Integer>> getCommunitysByInteger() {
		return GraphUtils.exportCommunityCollection(graph);
	}
	public Collection<Collection<Vertex>> getCommunitysByVertex() {
		return GraphUtils.exportCommunityCollectionWithVertex(graph);
	}
	public Collection<Entry<String, Collection<Integer>>> getCommunityByKeyValue() {
		return GraphUtils.exportCommunityEntrySet(graph);
	}

}
