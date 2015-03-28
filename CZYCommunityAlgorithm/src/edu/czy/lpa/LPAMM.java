package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public class LPAMM extends LPA{
	
	public LPAMM(SparseGraph<Vertex, Edge> g) {
		this(g, 100);
		// TODO Auto-generated constructor stub
	}
	public LPAMM(SparseGraph<Vertex, Edge> g, int itera) {
		super(g, itera, true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		//find the community structure by LPAM
		LPAM lpam = new LPAM(this.graph,this.iteration);
		lpam.run();
		HashMap<String,Collection<Integer>> comMap = new HashMap<String,Collection<Integer>>();
		HashMap<String,Collection<Integer>> comComparedMap = new HashMap<String,Collection<Integer>>();
		for(Entry<String,Collection<Integer>> entry:lpam.getCommunityByKeyValue()) {
			comMap.put(entry.getKey(), entry.getValue());
		}
		
		boolean isUpdated = true;
		//merge community pairs to maximun modularity
		while(true) {
			comComparedMap.clear();
			comComparedMap.putAll(comMap);
			
			double max_deltaQ = 0.0;
			String max_coms_id = "";//com1id+com2id
			//calculate all community pairs
			double current_Q = 
						MeasureCollections.calculateQFromCollectionsWithInteger(graph, comMap.values(),this.nodeMap);
//			System.out.println(current_Q);
			for(Entry<String,Collection<Integer>> com1:comMap.entrySet()) {
				for(Entry<String,Collection<Integer>> com2:comMap.entrySet()) {
					if(!com1.getKey().trim().equals(com2.getKey().trim())&&(Integer.valueOf(com1.getKey())<Integer.valueOf(com2.getKey()))){
						String newComId = com1.getKey().trim()+"+"+com2.getKey().trim();
						Set<Integer> newCom = new HashSet<Integer>();
						newCom.addAll(com1.getValue());
						newCom.addAll(com2.getValue());
						comComparedMap.remove(com1.getKey());
						comComparedMap.remove(com2.getKey());
						comComparedMap.put(newComId, newCom);
						double newQ = MeasureCollections.calculateQFromCollectionsWithInteger(graph, comComparedMap.values(),this.nodeMap);
						//System.out.println(newQ);
						if(newQ-current_Q > 0){
							if(max_deltaQ < (newQ-current_Q)){
								max_deltaQ = newQ-current_Q;
								max_coms_id = newComId;
							}
						}
						//recover
						comComparedMap.remove(newComId);
						comComparedMap.put(com1.getKey(), com1.getValue());
						comComparedMap.put(com2.getKey(), com2.getValue());
					}
				}
			}
//			System.out.println(max_coms_id);
			if(max_deltaQ > 0.0 && !"".equals(max_coms_id)) {
				//merge com_i and com_j
				String[] ids = max_coms_id.split("\\+");
				Set<Integer> newCom = new HashSet<Integer>();
				newCom.addAll(comMap.get(ids[0]));
				newCom.addAll(comMap.get(ids[1]));
				comMap.remove(ids[0]);
				comMap.remove(ids[1]);
				comMap.put(ids[0], newCom);	
			}else {
				break;
			}
		}
		//update nodes final label
		for(Entry<String,Collection<Integer>> com:comMap.entrySet()){
			for(Integer id:com.getValue()){
				this.nodeMap.get((long)id).setValue(com.getKey());
			}
		}
	}
	@Override
	public boolean updatelabel(Vertex[] v) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void  main(String[] args) {
		String filename="E:\\dataset\\unweight_dataset\\karate\\karate.gml";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		LPA lpamm = new LPAMM(graph,10000);
		lpamm.run();
		Collection<Collection<Vertex>> coms = lpamm.getCommunitysByVertex();
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q=MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularrity Q="+Q);
	}

}
