package edu.czy.algorithm;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.czy.lpa.LPA;
import edu.czy.lpa.LPAM;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;


public class LFM {
	private List<Collection<Vertex>> communities;
	private Collection<Vertex> remain;
	private Collection<Vertex> overlap;
	private SparseGraph<Vertex,Edge> graph;
	private Map<Long,Vertex> nodemap;
	public LFM(SparseGraph<Vertex,Edge> graph) {
		communities = new ArrayList<Collection<Vertex>>();
		this.graph = graph;
		this.nodemap = new HashMap<Long,Vertex>();
		for(Vertex v:this.graph.getVertices()) {
			this.nodemap.put(v.getId(), v);
		}
	}

	public void findCommunity() {
		Set<Vertex> cover = new HashSet<Vertex>();
		Collection<Vertex> temp = graph.getVertices();
		Collection<Vertex> group;
		int iterative = 0;
		Vertex startnode = getSeed(temp);
		try {
			do {
				System.out.println("StartNode" + (++iterative) + "="
						+ startnode.getId());
				FMethodNaive fm = new FMethodNaive(graph,this.nodemap);
				fm.findCommunityOf(startnode.getId());
				group = fm.getCommunityLoose();
				if (group != null) {
					communities.add(group);
					cover.addAll(group);
					remain = differenceSet(temp, cover);
					cover.clear();
				}
				if (!remain.isEmpty()) {
					startnode = getSeed(remain);
				} else {
					break;
				}
				temp = remain;
			} while (true);
			this.mergeCommunity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vertex getSeed(Collection<Vertex> com) {
		List<Vertex> cm = new ArrayList<Vertex>(com);
		int seed = (int) (Math.random() * com.size());
		Vertex seedv = cm.get(seed);
		cm.clear();
		return seedv;
	}

	private void mergeCommunity() {
		System.out.println("starting to mege communities ......");
		for (int i = 0; i < communities.size() - 1; i++) {
			int j = i + 1;
			while (j < communities.size()) {
				if (communityIntersectionRatio(communities.get(i),
						communities.get(j)) >= GraphUtils.minIntersectionRatio) {
					merge(communities.get(i), communities.get(j));
					communities.remove(j);
				} else {
					j++;
				}
			}
		}
		System.out.println("end to merge communities ......");
	}

	private double communityIntersectionRatio(Collection<Vertex> c1,
			Collection<Vertex> c2) {
		int count = 0;
		if (c1.size() > c2.size()) {
			for (Vertex v2 : c2) {
				if (c1.contains(v2))
					count++;
			}
			return (double) count / c2.size();
		} else {
			for (Vertex v1 : c1) {
				if (c2.contains(v1)) {
					count++;
				}
			}
			return (double) count / c1.size();
		}
	}

	private void merge(Collection<Vertex> c1, Collection<Vertex> c2) {
		for (Vertex v2 : c2) {
			if (!c1.contains(v2)) {
				c1.add(v2);
			}
		}
		c2.clear();
	}

	public Collection<Vertex> differenceSet(Collection<Vertex> c1,
			Collection<Vertex> c2) {
		Collection<Vertex> c = new ArrayList<Vertex>(c1);
		c.removeAll(c2);
		return c;
	}

	public List<Collection<Vertex>> getCommunities() {
		return communities;
	}

	public Collection<Vertex> getOutlier() {
		return remain;
	}

	public Collection<Vertex> getOverlap() {
		overlap = new ArrayList<Vertex>();
		Set<Vertex> set = new HashSet<Vertex>();
		for (int i = 0; i < communities.size(); i++) {
			for (Vertex v : communities.get(i)) {
				if (set.contains(v)) {
					overlap.add(v);
				} else {
					set.add(v);
				}
			}
		}
		return overlap;
	}

	public static void  main(String[] args) {
		String gmlfilename="J:\\paperproject\\DataSet\\karate\\karate.gml";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> graph=loadGML.loadGraph(gmlfilename);
		LFM lfm = new LFM(graph);
		lfm.findCommunity();
		Collection<Collection<Vertex>> coms = lfm.getCommunities();
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q=MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularrity Q="+Q);
	}

}
