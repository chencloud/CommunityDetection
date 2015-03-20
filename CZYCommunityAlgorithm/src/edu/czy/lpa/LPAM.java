package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

public class LPAM extends LPA{
	private Map<String,Integer> communityDegree;
	public LPAM(SparseGraph<Vertex, Edge> g) {
		this(g, 100);
	}

	public LPAM(SparseGraph<Vertex, Edge> g, int iteration) {
		super(g, iteration);
		this.communityDegree = new HashMap<String,Integer>();
		this.iteration = iteration;
		init();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		initNodeLabel();
	}

	private void initNodeLabel() {
		// TODO Auto-generated method stub
		for (Vertex v : this.graph.getVertices()) {
			v.setValue(String.valueOf(v.getId()));
			this.communityDegree.put(String.valueOf(v.getId()), this.graph.degree(v));
		}
	}

	@Override
	public boolean updatelabel(Vertex[] nodes) {
		// TODO Auto-generated method stub
		// Update the label of each vertex according to its neigbors' label
		Map<String, Double> hashmap = new HashMap<String, Double>();
		List<String> arrayLabels = new ArrayList<String>();
		boolean isUpdated = false;
		for (int i = 0; i < nodes.length; i++) {
			Vertex curV = nodes[i];
			Collection<Vertex> Vneighbors = this.graph.getNeighbors(curV);
			double maxCount = 0;
			hashmap.clear();
			arrayLabels.clear();
			for (Vertex v : Vneighbors) {
				String Vlabel = v.getValue();
				if (!hashmap.containsKey(Vlabel))
					hashmap.put(Vlabel, (-0.5)*this.graph.degree(curV)*this.communityDegree.get(Vlabel)/(1.0*this.graph.getEdgeCount()));
				double count = hashmap.get(Vlabel) + 1.0;
					hashmap.put(Vlabel, count);
				if (hashmap.get(Vlabel) - maxCount > 0.0) {
					maxCount = hashmap.get(Vlabel);
					arrayLabels.clear();
					arrayLabels.add(Vlabel);
				} else if(Math.abs(maxCount-hashmap.get(Vlabel))<=GraphUtils.eps) {
					arrayLabels.add(Vlabel);
				}
			}
			if(arrayLabels.size()>1) {
				//if the one of the max labels equals to curV'label ,then choose this label
				// random choose one
				int rIndex =RandomNumGenerator.getRandomInt(arrayLabels.size());
				if(!curV.getValue().trim().equals(arrayLabels.get(rIndex).trim())){
					curV.setValue(arrayLabels.get(rIndex));
					isUpdated = true;
				}
			}else {
				if(!curV.getValue().trim().equals(arrayLabels.get(0).trim())){
					curV.setValue(arrayLabels.get(0));
					isUpdated = true;
				}
			}

		}
		if (!isUpdated) {
			return false;
		}
		return true;
	}
}
