/**
 * 
 */
package edu.czy.algorithm;

import java.util.*;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;


/**
 * @author fangming
 * @date 2011-7-28
 * @version 1.0
 */
public class FMethodNaive  {
	private Vertex startNode;
	private int Ein, Eout;
	private List<Vertex> Community;
	private List<Vertex> Neighbors;
	private SparseGraph<Vertex,Edge> graph;
	private Map<Long,Vertex> nodeMap;
	public FMethodNaive(SparseGraph<Vertex,Edge> graph,Map<Long,Vertex> nodeMap) {
		this.graph = graph;
		this.nodeMap = nodeMap;
		Community = new ArrayList<Vertex>();
		Neighbors = new ArrayList<Vertex>();
	}

	public void initialState() {
		Community.clear();
		Neighbors.clear();
		Community.add(startNode);
		Neighbors.addAll(graph.getNeighbors(startNode));
		Ein = 0;
		Eout = graph.degree(startNode);
	}

	public void findCommunityOf(Long id) {
		this.startNode = getVertexById(id);
		this.initialState();
		Vertex vertex = null;
		Vertex maxVertex = null;
		double addition;
		double maxAddition = 0;
		do {
			// include step:
			maxAddition = 0;
			Collections.sort(Neighbors, new Comparator<Vertex>() {
				@Override
				public int compare(Vertex v0, Vertex v1) {
					return graph.degree(v0) - graph.degree(v1);
				}
			});
			for (int i = 0; i < Neighbors.size(); i++) {
				vertex = Neighbors.get(i);
				addition = this.beforeIncludeStep(vertex);
				if (addition > maxAddition) {
					maxAddition = addition;
					maxVertex = vertex;
				}
			}
			if (maxAddition > 0) {
				this.afterIncludeStep(maxVertex);
				Community.add(maxVertex);
				for (Vertex n : graph.getNeighbors(maxVertex)) {
					if (!Community.contains(n) && !Neighbors.contains(n)) {
						Neighbors.add(n);
					}
				}
				Neighbors.remove(maxVertex);
			} else {
				break;
			}
			// exclude step:
			while (true) {
				maxAddition = 0;
				for (int j = 0; j < Community.size(); j++) {
					vertex = Community.get(j);
					addition = this.beforeExcludeStep(vertex);
					if (addition > maxAddition) {
						maxAddition = addition;
						maxVertex = vertex;
					}
				}
				if (maxAddition > 0) {
					this.afterExcludeStep(maxVertex);
					Community.remove(maxVertex);
				} else {
					break;
				}
			}
		} while (true);
	}

	private Vertex getVertexById(Long id) {
		// TODO Auto-generated method stub
		return this.nodeMap.get(id);
	}

	public double beforeIncludeStep(Vertex v) {
		int ein = 0;
		int eout;
		Iterator<Vertex> it = graph.getNeighbors(v).iterator();
		while (it.hasNext()) {
			Vertex n = it.next();
			if (Community.contains(n)) {
				ein++;
			}
		}

		eout = graph.degree(v) - 2 * ein;
		ein *= 2;
		return (double) (Ein + ein)
				/ Math.pow((Ein + ein + Eout + eout), GraphUtils.coefficient)
				- (double) Ein / Math.pow(Ein + Eout, GraphUtils.coefficient);
	}

	public void afterIncludeStep(Vertex v) {
		int ein = 0;
		int eout;
		Iterator<Vertex> it = graph.getNeighbors(v).iterator();
		while (it.hasNext()) {
			Vertex n = it.next();
			if (Community.contains(n)) {
				ein++;
			}
		}
		eout = graph.degree(v) - 2 * ein;
		ein *= 2;
		Ein = Ein + ein;
		Eout = Eout + eout;
	}

	public double beforeExcludeStep(Vertex v) {
		int ein = 0;
		int eout;
		Iterator<Vertex> it = graph.getNeighbors(v).iterator();
		while (it.hasNext()) {
			Vertex n = it.next();
			if (Community.contains(n)) {
				ein++;
			}
		}
		eout = graph.inDegree(v) - 2 * ein;
		ein *= 2;
		return (double) (Ein - ein)
				/ Math.pow((Ein - ein + Eout - eout), GraphUtils.coefficient)
				- (double) Ein / Math.pow((Ein + Eout), GraphUtils.coefficient);
	}

	public void afterExcludeStep(Vertex v) {
		int ein = 0;
		int eout;
		Iterator<Vertex> it = graph.getNeighbors(v).iterator();
		while (it.hasNext()) {
			Vertex n = it.next();
			if (Community.contains(n)) {
				ein++;
			}
		}
		eout = graph.inDegree(v) - 2 * ein;
		ein *= 2;
		Ein = Ein - ein;
		Eout = Eout - eout;
	}

	public void printCommunity() {
		if (getCommunity() != null) {
			System.out.print("The Community of " + startNode.getId() + " = [ ");
			//sortByID(Community);
			for (Vertex v : Community) {
				System.out.print(v.getId() + " ");
			}
			System.out.println("]");
			System.out.println("F=" + (double) Ein
					/ Math.pow(Ein + Eout, GraphUtils.coefficient));
		}
	}

	public List<Vertex> getCommunityLoose() {
		return Community;
	}

	public List<Vertex> getCommunity() {
		double F = (double) Ein / Math.pow(Ein + Eout, GraphUtils.coefficient);
		if (F > 0 && Community.contains(startNode)
				&& Community.size() >= GraphUtils.minCommunity)
			return Community;
		else {
			System.out.println("There is no local community found for "
					+ startNode.getId());
			return null;
		}
	}

	public static void main(String[] args) {
//		String fileName = "E:\\NetworkDataset\\karate\\karate.gml";
//		Main.loadGraph(fileName,0);
//		
//		LocalMethod m = new FMethodNaive();
//		m.findCommunityOf(1);
//		m.printCommunity();
	}

}
