package edu.czy.overlap.lpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.lpa.LPA;
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
	}
	public SLPA(SparseGraph<Vertex, Edge> g, int itera,double r) {
		super(g, itera);
		this.r = r;
		// TODO Auto-generated constructor stub
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
		//更新value值
		for(int i=0;i<vs.length;i++) {
			String value = "";
			for(Entry<Long,Double> entry:vs[i].getCommunityDistribution().entrySet()) {
				if("".equals(value)) {
					value = String.valueOf(entry.getKey());
				} else {
					value = value + GraphUtils.OverlapNode_Split + String.valueOf(entry.getKey());
				}
			}
			vs[i].setValue(value);
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
		
	}

}
