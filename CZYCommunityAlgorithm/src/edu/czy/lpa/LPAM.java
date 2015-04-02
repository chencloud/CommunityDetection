package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

public class LPAM extends LPA{
	private Map<String,Integer> communityDegree;
	public LPAM(SparseGraph<Vertex, Edge> g) {
		this(g, 100);
	}

	public LPAM(SparseGraph<Vertex, Edge> g, int iteration) {
		super(g, iteration,false);
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
			double maxCount = Double.MIN_VALUE;
			hashmap.clear();
			arrayLabels.clear();
			for (Vertex v : Vneighbors) {
				String Vlabel = v.getValue();
				if (!hashmap.containsKey(Vlabel))
					hashmap.put(Vlabel, (-0.5)*this.graph.degree(curV)*this.communityDegree.get(Vlabel)/(1.0*this.graph.getEdgeCount()));
				double count = hashmap.get(Vlabel) + 1.0;
				hashmap.put(Vlabel, count);
			}
			for(Entry<String,Double> entry : hashmap.entrySet()){
				String Vlabel = entry.getKey();
				double value = entry.getValue();
				if (value - maxCount > 0.0) {
					maxCount = value;
					arrayLabels.clear();
					arrayLabels.add(Vlabel);
				} else if(Math.abs(maxCount-value)<=GraphUtils.eps) {
					arrayLabels.add(Vlabel);
				}
			}
			if(arrayLabels.size()>1) {
				//if the one of the max labels equals to curV'label ,then choose this label
				// random choose one
				int rIndex =RandomNumGenerator.getRandomInt(arrayLabels.size());
				if(!curV.getValue().trim().equals(arrayLabels.get(rIndex).trim())){
					if(this.communityDegree.containsKey(curV.getValue().trim())) {
						this.communityDegree.put(curV.getValue().trim(),this.communityDegree.get(arrayLabels.get(rIndex))-this.graph.degree(curV));
					}
					curV.setValue(arrayLabels.get(rIndex));
					int curVD = this.graph.degree(curV);
					if(this.communityDegree.containsKey(arrayLabels.get(rIndex)))
						curVD += this.communityDegree.get(arrayLabels.get(rIndex));
					this.communityDegree.put(arrayLabels.get(rIndex), curVD);
					isUpdated = true;
				}
			}else {
				if(!curV.getValue().trim().equals(arrayLabels.get(0).trim())){
					if(this.communityDegree.containsKey(curV.getValue().trim())) {
						this.communityDegree.put(curV.getValue().trim(),this.communityDegree.get(arrayLabels.get(0))-this.graph.degree(curV));
					}
					curV.setValue(arrayLabels.get(0));
					int curVD = this.graph.degree(curV);
					if(this.communityDegree.containsKey(arrayLabels.get(0)))
						curVD += this.communityDegree.get(arrayLabels.get(0));
					this.communityDegree.put(arrayLabels.get(0), curVD);
					isUpdated = true;
				}
			}

		}
		if (!isUpdated) {
			return false;
		}
		return true;
	}
	public static void  main(String[] args) {
		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";
//		String filename="E:\\dataset\\unweight_dataset\\toy_network\\toy_network.net";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		LPA lpam = new LPAM(graph,1000);
		lpam.run();
		Collection<Collection<Vertex>> coms = lpam.getCommunitysByVertex();
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q=MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularrity Q="+Q);
	}
}
