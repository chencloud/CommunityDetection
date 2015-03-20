package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

/**
 * 
 * @author CHENZHIYUN
 * 
 *         Standard Label Propagation Algorithm
 */
public class StandardLPA extends LPA {

	public StandardLPA(SparseGraph<Vertex, Edge> g) {
		this(g, 100);
	}

	public StandardLPA(SparseGraph<Vertex, Edge> g, int iteration) {
		super(g, iteration);
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
		}
	}

	@Override
	public boolean updatelabel(Vertex[] nodes) {
		// TODO Auto-generated method stub
		// Update the label of each vertex according to its neigbors' label
		Map<String, Integer> hashmap = new HashMap<String, Integer>();
		List<String> arrayLabels = new ArrayList<String>();
		boolean isUpdated = false;
		for (int i = 0; i < nodes.length; i++) {
			Vertex curV = nodes[i];
			Collection<Vertex> Vneighbors = this.graph.getNeighbors(curV);
			int maxCount = 0;
			hashmap.clear();
			arrayLabels.clear();
			for (Vertex v : Vneighbors) {
				String Vlabel = v.getValue();
				if (!hashmap.containsKey(Vlabel))
					hashmap.put(Vlabel, 0);
				int count = hashmap.get(Vlabel) + 1;
					hashmap.put(Vlabel, count);
				if (maxCount < hashmap.get(Vlabel)) {
					maxCount = hashmap.get(Vlabel);
					arrayLabels.clear();
					arrayLabels.add(Vlabel);
				} else if(maxCount == hashmap.get(Vlabel)) {
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
