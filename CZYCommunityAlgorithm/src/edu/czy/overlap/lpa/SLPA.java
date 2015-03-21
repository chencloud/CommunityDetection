package edu.czy.overlap.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.czy.lpa.LPA;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * 采用异步更新策略
 */
public class SLPA extends LPA{

	public double r = 0.3;
	public SLPA(SparseGraph<Vertex, Edge> g, int itera) {
		super(g, itera);
		// TODO Auto-generated constructor stub
		init();
	}
	public SLPA(SparseGraph<Vertex, Edge> g, int itera,double r) {
		super(g, itera);
		this.r = r;
		// TODO Auto-generated constructor stub
		init();
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		//不采用节点里面的value,采用communityDistribution
		Vertex[] vs = this.graph.getVertices().toArray(new Vertex[0]);
		for(int i=0;i<vs.length;i++) {
			vs[i].getCommunityDistribution().clear();
			vs[i].getCommunityDistribution().put(vs[i].getId(), 1.0);
		}
		
	}
	@Override
	public void run() {
		Vertex[] vs = this.graph.getVertices().toArray(new Vertex[0]);
		for(int i=1;i<=this.iteration;i++) {
//			System.out.println("the "+i+"th iteration");
			updatelabel(vs);
		}
		//后处理步骤
		for(int i=0;i<vs.length;i++) {
			for(Entry<Long,Double> entry:vs[i].getCommunityDistribution().entrySet()) {
				Long comId = entry.getKey();
				double community_blongness = entry.getValue();
				if(community_blongness < this.r) {
					vs[i].getCommunityDistribution().remove(comId);
				}
			}
		}
		//合并社區
		Map<Long,Collection<Vertex>> coms = new HashMap<Long,Collection<Vertex>>();
		
		for(int i=0;i<vs.length;i++) {
			vs[i].setValue(null);
			for(Entry<Long,Double> entry:vs[i].getCommunityDistribution().entrySet()) {
				Long comId = entry.getKey();
				if(!coms.containsKey(comId)) {
					coms.put(comId, new ArrayList<Vertex>());
				}
				coms.get(comId).add(vs[i]);
			}
		}

		List<Entry<Long,Collection<Vertex>>> comms = new ArrayList<Entry<Long,Collection<Vertex>>>(coms.entrySet());
//		for(Entry<Long,Collection<Vertex>> com:comms) {
//			System.out.println(com.getKey());
//			for(Vertex v:com.getValue()){
//				System.out.print(v.getId()+"\t");
//			}
//			System.out.println();
//		}
//		System.out.println(comms.size());
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
				System.out.println(isSame);
				if(isSame){
					if(comms.get(i).getValue().size()<comms.get(j).getValue().size()){
						comms.get(i).getValue().clear();
						comms.get(i).getValue().addAll(comms.get(j).getValue());
					}
					System.out.println(comms.get(j).getKey());
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
	}
	@Override
	public boolean updatelabel(Vertex[] v) {
		// TODO Auto-generated method stub
		for(int i=0;i<v.length;i++) {
			Vertex listener = v[i];
			HashMap<Long, Double> incomingVotes = new HashMap<Long, Double>();
			for(Vertex neigV : this.graph.getNeighbors(listener)) {
				//根据标签出现的频次随机选择一个
				Long votedCommunity = speakerVote(neigV);
				double votedCommunitycount = 1;
				if ( incomingVotes.containsKey(votedCommunity) ) 
					votedCommunitycount += incomingVotes.get(votedCommunity);
				
				incomingVotes.put(votedCommunity, votedCommunitycount);
				
			}
			
			//Find the most popular vote
			Iterator<Entry<Long, Double>> it = incomingVotes.entrySet().iterator();
			Long popularCommunity=-1L;
			double popularCommunityCount=0;
			while ( it.hasNext() ) {
				Entry<Long, Double> entry = it.next();
				if ( entry.getValue() > popularCommunityCount ) {
					popularCommunity = entry.getKey();
					popularCommunityCount = entry.getValue();
				}
			}
			
			//Update community distribution of the current node by 1
			listener.updateCommunityDistribution(popularCommunity, 1.0);
		}
		
		return true;
	}
	//Vote for a community randomly based on the distribution of communities at this node
	public Long speakerVote(Vertex curV) {
		
		//Run through each element in the map to create a cumulative distribution
		Map<Long,Double> communityDistribution = curV.getCommunityDistribution();
		Set<Long> communityIds = communityDistribution.keySet();
		ArrayList<Long> communities = new ArrayList<Long>();
		ArrayList<Double> cumulativeCounts = new ArrayList<Double>();
		
		double sum=-1;
		for (Long comm: communityIds) {
			sum += communityDistribution.get(comm);
			communities.add(comm);
			cumulativeCounts.add(sum);
		}
		//Generate a random integer in the range [0,sum)
		int rand = edu.czy.utils.RandomNumGenerator.getRandomInt((int)sum+1);
		
		//Find the index of first value greater than rand in cumulativeCounts
		int i=0;
		for (i=0; i<cumulativeCounts.size(); i++) 
			if (cumulativeCounts.get(i)>=rand) 
				break;
		
		//Return the corresponding community
		return communities.get(i);
	}
	
	public static void main(String args[]) {
		String gmlfilename="J:\\paperproject\\DataSet\\karate\\karate.gml";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> graph=loadGML.loadGraph(gmlfilename);
		double r = 0.3;
		//for(double r=0.1;r<=0.5;r+=0.01){
			SLPA slpa = new SLPA(graph,10000,r);
			slpa.run();
			Collection<Collection<Vertex>> coms = slpa.getCommunitysByVertex();
			GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
			double Q = MeasureCollections.calculateQovWithVertex(coms, graph);
			System.out.println("r="+r+" ;Modularity Qov = "+Q);
		//}
	}

}
