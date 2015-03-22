package edu.czy.overlap.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.lpa.LPA;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * 2013 MLPA Detecting Overlapping Communities by Multi Label Propagation
 */
public class MLPA extends LPA{
	private int labelNum ;
	private double p;
	public boolean isNormal = true;
	public boolean isASync = true; 
	public MLPA(SparseGraph<Vertex, Edge> g, int itera,double p) {
		super(g, itera);
		this.p = p;
		// TODO Auto-generated constructor stub
		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		//不采用节点里面的value,采用communityDistribution
		Vertex[] vs = this.graph.getVertices().toArray(new Vertex[0]);
		for(int i=0;i<vs.length;i++) {
			vs[i].setValue(null);
			vs[i].getCommunityDistribution().clear();
			vs[i].getCommunityDistribution().put(vs[i].getId(), 1.0);
		}
		this.labelNum = vs.length;
	}
	@Override
	public void run() {
		Vertex[] vs = this.graph.getVertices().toArray(new Vertex[0]);	
		for(int i=1;i<=this.iteration;i++) {
			//shuffle the order for node's update (maybe not necessary)
			int nodecount=vs.length;
			//shuffle card
			for(int j=--nodecount;j>=0;j--)
			{
				int index=RandomNumGenerator.getRandomInt(j+1);
				Vertex tempV=vs[index];
				vs[index]=vs[j];
				vs[j]=tempV;
			}
//			System.out.println("the "+i+"th iteration");
			if(this.isASync){
				if(!updatelabel(vs)){
					break;
				}
			}
			else{
				if(!updatelabelSync(vs)){
					break;
				}
			}
		}
		
//		//后处理步骤，合并社區
		Map<Long,Collection<Vertex>> coms = new HashMap<Long,Collection<Vertex>>();
		
		for(int i=0;i<vs.length;i++) {
			for(Entry<Long,Double> entry:vs[i].getCommunityDistribution().entrySet()) {
				Long comId = entry.getKey();
				if(!coms.containsKey(comId)) {
					coms.put(comId, new ArrayList<Vertex>());
				}
				coms.get(comId).add(vs[i]);
			}
		}
		List<Long> tempDelete = new ArrayList<Long>();
		for(Entry<Long,Collection<Vertex>> entry:coms.entrySet()) {
			if(entry.getValue().size() == vs.length)
				tempDelete.add(entry.getKey());
		}
		if(!tempDelete.isEmpty()){
			for(Long delId:tempDelete){
				coms.remove(delId);
			}
		}
		List<Entry<Long,Collection<Vertex>>> comms = new ArrayList<Entry<Long,Collection<Vertex>>>(coms.entrySet());
		System.out.println("before Community Size="+comms.size());
		for(int i=0;i<comms.size();i++) {
			for(int j=i+1;j<comms.size();) {
				boolean isSame = true;
				if(comms.get(i).getValue().size()>=comms.get(j).getValue().size()){
					for(Vertex v :comms.get(j).getValue()) {
						boolean isISame =false;
						for(Vertex v1:comms.get(i).getValue()){
							if(v.getId()==v1.getId()){
								isISame = true;
								break;
							}
						}
						if(!isISame) {
							isSame = false; 
							break;
						}
					}
				}else {
					for(Vertex v :comms.get(i).getValue()) {
						boolean isISame =false;
						for(Vertex v1:comms.get(j).getValue()){
							if(v.getId()==v1.getId()){
								isISame = true;
								break;
							}
						}
						if(!isISame) {
							isSame = false;
							break;
						}
					}
				}
//				System.out.println(isSame);
				if(isSame){
					if(comms.get(i).getValue().size()<comms.get(j).getValue().size()){
						comms.get(i).getValue().clear();
						comms.get(i).getValue().addAll(comms.get(j).getValue());
					}
//					System.out.println(comms.get(j).getKey());
					comms.remove(j);
					j= i+1;
				}
				else {
					j++;
				}
			}
		}
		System.out.println("Cur Community Size="+comms.size());
		
		//更新value值
		for(int i=0;i<comms.size();i++) {
			Long ComId = comms.get(i).getKey();
			for(Vertex v:comms.get(i).getValue()) {
				if(null == v.getValue() ||"".equals(v.getValue())){
					v.setValue(String.valueOf(ComId));
				}
				else {
					v.setValue(v.getValue()+GraphUtils.OverlapNode_Split+String.valueOf(ComId));
				}
			}
		}
		
		//处理孤立点
		
	}
	private boolean updatelabelSync(Vertex[] v) {
		// TODO Auto-generated method stub
		//保存上一次的节点标签结果
		Map<Long,Map<Long,Double>> node_com_map = new HashMap<Long,Map<Long,Double>>();
		for(int i=0;i<v.length;i++) {
			node_com_map.put(v[i].getId(), new HashMap<Long,Double>());
			node_com_map.get(v[i].getId()).putAll(v[i].getCommunityDistribution());
		}
		Set<Long> labelSet = new HashSet<Long>();
		for(int i=0;i<v.length;i++) {
			Vertex listener = v[i];
			HashMap<Long, Double> incomingVotes = new HashMap<Long, Double>();
			for(Vertex neigV : this.graph.getNeighbors(listener)) {
				for(Entry<Long,Double> c:node_com_map.get(neigV.getId()).entrySet()){
					//
					double CommunityValue = Math.sqrt(Jaccard(listener,neigV)*c.getValue());
					if ( incomingVotes.containsKey(c.getKey()) ) 
						CommunityValue += incomingVotes.get(c.getKey());
					
					incomingVotes.put(c.getKey(), CommunityValue);
				}
				
			}
			
			//Find the most popular vote
			Iterator<Entry<Long, Double>> it = incomingVotes.entrySet().iterator();
			double popularCommunityValue=0;
			while ( it.hasNext() ) {
				Entry<Long, Double> entry = it.next();
				if ( entry.getValue() > popularCommunityValue ) {
					popularCommunityValue = entry.getValue();
				}
			}
			double throld_c_max = popularCommunityValue*this.p;
			Collection<Entry<Long, Double>> cols = new ArrayList<Entry<Long, Double>>();
			cols.addAll(incomingVotes.entrySet());
			
			for ( Entry<Long, Double> col:cols ) {
				if ( col.getValue() < throld_c_max ) {
					incomingVotes.remove(col.getKey());
				}
			}
			//Update community distribution of the current node by 1
			Iterator<Entry<Long, Double>> filteredit = incomingVotes.entrySet().iterator();
			listener.getCommunityDistribution().clear();
			double sumValue = 0.0;
			while ( filteredit.hasNext() ) {
				Entry<Long, Double> entry = filteredit.next();
				sumValue += entry.getValue();
			}
			filteredit = incomingVotes.entrySet().iterator();
			while ( filteredit.hasNext() ) {
				Entry<Long, Double> entry = filteredit.next();
				if(this.isNormal)
					listener.updateCommunityDistribution(entry.getKey(), entry.getValue()/sumValue);
				else 
					listener.updateCommunityDistribution(entry.getKey(), entry.getValue());
				labelSet.add(entry.getKey());
			}
			
		}
		if(labelSet.size() == this.labelNum)
			return false;
		else {
			this.labelNum = labelSet.size();return true;
		}
	}

	@Override
	public boolean updatelabel(Vertex[] v) {
		// TODO Auto-generated method stub
		Set<Long> labelSet = new HashSet<Long>();
		for(int i=0;i<v.length;i++) {
			Vertex listener = v[i];
			HashMap<Long, Double> incomingVotes = new HashMap<Long, Double>();
			for(Vertex neigV : this.graph.getNeighbors(listener)) {
				for(Entry<Long,Double> c:neigV.getCommunityDistribution().entrySet()){
					//
					double CommunityValue = Math.sqrt(Jaccard(listener,neigV)*c.getValue());
					if ( incomingVotes.containsKey(c.getKey()) ) 
						CommunityValue += incomingVotes.get(c.getKey());
					
					incomingVotes.put(c.getKey(), CommunityValue);
				}
				
			}
			
			//Find the most popular vote
			Iterator<Entry<Long, Double>> it = incomingVotes.entrySet().iterator();
			double popularCommunityValue=0;
			while ( it.hasNext() ) {
				Entry<Long, Double> entry = it.next();
				if ( entry.getValue() > popularCommunityValue ) {
					popularCommunityValue = entry.getValue();
				}
			}
			double throld_c_max = popularCommunityValue*this.p;
			Collection<Entry<Long, Double>> cols = new ArrayList<Entry<Long, Double>>();
			cols.addAll(incomingVotes.entrySet());
			for ( Entry<Long, Double> col:cols ) {
				if ( col.getValue() < throld_c_max ) {
					incomingVotes.remove(col.getKey());
				}
			}
			//Update community distribution of the current node by 1
			Iterator<Entry<Long, Double>> filteredit = incomingVotes.entrySet().iterator();
			listener.getCommunityDistribution().clear();
			double sumValue = 0.0;
			while ( filteredit.hasNext() ) {
				Entry<Long, Double> entry = filteredit.next();
				sumValue += entry.getValue();
			}
			filteredit = incomingVotes.entrySet().iterator();
			while ( filteredit.hasNext() ) {
				Entry<Long, Double> entry = filteredit.next();
				if(this.isNormal)
					listener.updateCommunityDistribution(entry.getKey(), entry.getValue()/sumValue);
				else 
					listener.updateCommunityDistribution(entry.getKey(), entry.getValue());
				labelSet.add(entry.getKey());
			}
			
		}
		if(labelSet.size() == this.labelNum)
			return false;
		else {
			this.labelNum = labelSet.size();return true;
		}
		
	}

	private double Jaccard(Vertex listener, Vertex speaker) {
		// TODO Auto-generated method stub
		Collection<Vertex> N1 = this.graph.getNeighbors(listener);
		Collection<Vertex> N2 = this.graph.getNeighbors(speaker);
		int intersectCount = 0;
		for(Vertex v1:N1) {
			for(Vertex v2:N2) {
				if(v1.getId()==v2.getId()){
					intersectCount++;
				}
			}
		}
		return intersectCount*1.0/Math.sqrt(N1.size()*N2.size()*1.0);
	}
	
	public static void main(String args[]) {
		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		double r=0.1;
		double maxQov = 0.0;
		double maxr = 0.0;
		for(;r<=1;r+=0.01)
		{
			MLPA mlpa = new MLPA(graph,10000000,r);
			mlpa.run();
			Collection<Collection<Vertex>> coms = mlpa.getCommunitysByVertex();
	//		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
			double Qov = MeasureCollections.calculateQovWithVertex(coms, graph);
			if(Qov > maxQov){
				maxQov = Qov;
				maxr = r;
			}
			System.out.println("r="+r+" ;Modularity Qov = "+Qov);
		}
		System.out.println("max r="+maxr+" ;Maximun Modularity Qov = "+maxQov);
	}
}
