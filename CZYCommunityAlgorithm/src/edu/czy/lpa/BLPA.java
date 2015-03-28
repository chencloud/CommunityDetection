package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

public class BLPA extends LPA{
	private static final double EPSION = 0.0000000001;
	public BLPA(SparseGraph<Vertex,Edge> graph, int iteration) {
		super(graph,iteration,false);
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
		}
	}

	@Override
	public boolean updatelabel(Vertex[] nodes) {
		// TODO Auto-generated method stub
		for(int i=0; i<nodes.length; i++) {
			nodes[i].getCommunityDistribution().put(nodes[i].getId(), 1.0*(i+1)/nodes.length);
		}
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
				double pv = v.getCommunityDistribution().get(v.getId());
				if (!hashmap.containsKey(Vlabel))
					hashmap.put(Vlabel, 0.0);
				double count = hashmap.get(Vlabel) + pv;
				hashmap.put(Vlabel, count);
			}
			for(String Vlabel : hashmap.keySet()) {
				if (maxCount < hashmap.get(Vlabel)) {
					maxCount = hashmap.get(Vlabel);
					arrayLabels.clear();
					arrayLabels.add(Vlabel);
				} else if(Math.abs(maxCount - hashmap.get(Vlabel))<this.EPSION) {
						arrayLabels.add(Vlabel);
				}
			}
			if(arrayLabels.size()>1) {
				//if the one of the max labels equals to curV'label ,then choose this label
				boolean existedCurLabel = false;
				for(String vlabel:arrayLabels){
					if(curV.getValue().trim().equals(vlabel.trim())) {
						existedCurLabel = true;
						break;
					}
				}
				if(!existedCurLabel) {
					// random choose one
					int rIndex =RandomNumGenerator.getRandomInt(arrayLabels.size());
					curV.setValue(arrayLabels.get(rIndex));
					isUpdated = true;
				}

			}else {
//				System.out.println(arrayLabels.get(0).toString());
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
	public static void  main(String[] args) {
		String filename="E:\\dataset\\unweight_dataset\\jazz\\jazz.net";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		LPA blpa = new BLPA(graph,10000);
		blpa.run();
		Collection<Collection<Vertex>> coms = blpa.getCommunitysByVertex();
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q=MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularrity Q="+Q);
	}
}
