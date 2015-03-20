package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public abstract class LPA {
	protected SparseGraph<Vertex,Edge> graph;
	public LPA(SparseGraph<Vertex,Edge> g) {
		this.graph = g;
	}
	public abstract void init();
	public abstract void run();
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
