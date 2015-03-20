package edu.czy.measure;

//Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//Jad home page: http://www.kpdus.com/jad.html
//Decompiler options: packimports(3) 
//Source File Name:   ModOverlap.java

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;



public class ModularityOverlap {
	
	public SparseGraph<Vertex,Edge> graph;
	public ModularityOverlap(SparseGraph<Vertex,Edge> graph) {
		this.graph = graph;
	}

	public double modOverlap(Collection<Collection<Vertex>> comms) {
		int i = 0;
		int j = 0;
		HashMap hashmap = readClusters(comms);
		HashMap hashmap1 = readGraphEdges();
		for (Iterator iterator = hashmap1.keySet().iterator(); iterator
				.hasNext();) {
			Vertex s2 = (Vertex) iterator.next();
			j++;
			i += ((HashSet) hashmap1.get(s2)).size();
		}

		i /= 2;
		return modOverlap(hashmap, hashmap1, j, i);
	}

	private static void showClusters(HashMap hashmap, int i, int j) {
		System.out.println((new StringBuilder()).append("n = ").append(i)
				.append(", m = ").append(j).toString());
		System.out.println(hashmap);
		System.out.println(invertClusters(hashmap));
	}

	private static HashMap invertClusters(HashMap hashmap) {
		HashMap hashmap1 = new HashMap();
		for (Iterator iterator1 = hashmap.keySet().iterator(); iterator1
				.hasNext();) {
			int i = ((Integer) iterator1.next()).intValue();
			Iterator iterator = ((HashMap) hashmap.get(Integer.valueOf(i)))
					.keySet().iterator();
			while (iterator.hasNext()) {
				Vertex s = (Vertex) iterator.next();
				double d = ((Double) ((HashMap) hashmap.get(Integer.valueOf(i)))
						.get(s)).doubleValue();
				if (!hashmap1.containsKey(s))
					hashmap1.put(s, new HashMap());
				((HashMap) hashmap1.get(s)).put(Integer.valueOf(i),
						Double.valueOf(d));
			}
		}

		return hashmap1;
	}

	private HashMap readClusters(Collection<Collection<Vertex>> clusters) {
		Object obj = null;
		Object obj1 = null;
		HashMap hashmap1 = new HashMap();
		HashMap hashmap2 = new HashMap();
		int j = 0;
		for (Collection<Vertex> cluster : clusters) {
			j++;
			HashMap hashmap = new HashMap();
			for (Vertex v : cluster) {
				double d = 1.0D;
				hashmap.put(v, Double.valueOf(d));
				if (!hashmap1.containsKey(v))
					hashmap1.put(v, Double.valueOf(0.0D));
				hashmap1.put(
						v,
						Double.valueOf(((Double) hashmap1.get(v)).doubleValue()
								+ d));
			}

			hashmap2.put(Integer.valueOf(j), hashmap);
		}
		normalize(hashmap2, hashmap1);
		return hashmap2;
	}

	public HashMap readGraphEdges() {
		HashMap hashmap = new HashMap();
		for (Vertex v : graph.getVertices()) {
			HashSet set = new HashSet(graph.getNeighbors(v));
			hashmap.put(v, set);
		}
		return hashmap;
	}

	public void normalize(HashMap hashmap, HashMap hashmap1) {
		for (Iterator iterator = hashmap.keySet().iterator(); iterator
				.hasNext();) {
			HashMap hashmap2 = (HashMap) hashmap.get(iterator.next());
			Iterator iterator1 = hashmap2.keySet().iterator();
			while (iterator1.hasNext()) {
				Vertex s = (Vertex) iterator1.next();
				hashmap2.put(
						s,
						Double.valueOf(((Double) hashmap2.get(s)).doubleValue()
								/ ((Double) hashmap1.get(s)).doubleValue()));
			}
		}

	}

	public double modOverlap(HashMap hashmap, HashMap hashmap1,
			double d, double d1) {
		double d4 = 0.0D;
		for (Iterator iterator = hashmap.keySet().iterator(); iterator
				.hasNext();) {
			int i = ((Integer) iterator.next()).intValue();
			Iterator iterator2 = ((HashMap) hashmap.get(Integer.valueOf(i)))
					.keySet().iterator();
			while (iterator2.hasNext()) {
				Vertex s = (Vertex) iterator2.next();
				if (hashmap1.get(s) == null)
					System.out.println((new StringBuilder()).append("vertex ")
							.append(s).append(" not found in network")
							.toString());
				Iterator iterator5 = ((HashSet) hashmap1.get(s)).iterator();
				while (iterator5.hasNext()) {
					Vertex s3 = (Vertex) iterator5.next();
					d4 += F(s, s3, i, hashmap);
				}
			}
		}

		d4 /= d1 + d1;
		double d5 = 0.0D;
		for (Iterator iterator1 = hashmap.keySet().iterator(); iterator1
				.hasNext();) {
			int j = ((Integer) iterator1.next()).intValue();
			Set set = ((HashMap) hashmap.get(Integer.valueOf(j))).keySet();
			HashMap hashmap2 = new HashMap();
			HashMap hashmap3 = new HashMap();
			Vertex s1;
			for (Iterator iterator3 = set.iterator(); iterator3.hasNext(); hashmap3
					.put(s1, Double
							.valueOf(beta_in(s1, j, d, hashmap1, hashmap)))) {
				s1 = (Vertex) iterator3.next();
				hashmap2.put(s1,
						Double.valueOf(beta_out(s1, j, d, hashmap1, hashmap)));
			}

			Iterator iterator4 = set.iterator();
			while (iterator4.hasNext()) {
				Vertex s2 = (Vertex) iterator4.next();
				Iterator iterator6 = set.iterator();
				while (iterator6.hasNext()) {
					Vertex s4 = (Vertex) iterator6.next();
					double d3 = ((Double) hashmap2.get(s2)).doubleValue();
					double d2 = ((Double) hashmap3.get(s4)).doubleValue();
					int k = ((HashSet) hashmap1.get(s2)).size();
					int l = ((HashSet) hashmap1.get(s4)).size();
					d5 += d3 * d2 * (double) k * (double) l;
				}
			}
		}

		d5 /= 4D * d1 * d1;
		return d4 - d5;
	}

	private static double beta_out(Vertex s, int i, double d, HashMap hashmap,
			HashMap hashmap1) {
		double d2 = 0.0D;
		Set set = ((HashMap) hashmap1.get(Integer.valueOf(i))).keySet();
		if (!set.contains(s))
			return 0.0D;
		Iterator iterator = set.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Vertex s1 = (Vertex) iterator.next();
			if (!s.equals(s1))
				d2 += F(s, s1, i, hashmap1);
		} while (true);
		double d1 = d2 / (d - 1.0D);
		return d1;
	}

	private static double beta_in(Vertex s, int i, double d, HashMap hashmap,
			HashMap hashmap1) {
		double d2 = 0.0D;
		Set set = ((HashMap) hashmap1.get(Integer.valueOf(i))).keySet();
		if (!set.contains(s))
			return 0.0D;
		Iterator iterator = set.iterator();
		do {
			if (!iterator.hasNext())
				break;
			Vertex s1 = (Vertex) iterator.next();
			if (!s.equals(s1))
				d2 += F(s1, s, i, hashmap1);
		} while (true);
		double d1 = d2 / (d - 1.0D);
		return d1;
	}

	private static double F(Vertex s, Vertex s1, int i, HashMap hashmap) {
		double d1 = alpha(s, i, hashmap);
		double d2 = alpha(s1, i, hashmap);
		double d = 1.0D / ((1.0D + Math.exp(-f(d1))) * (1.0D + Math.exp(-f(d2))));
		return d;
	}

	private static double f(double d) {
		return 60D * d - 30D;
	}

	private static double alpha(Vertex s, int i, HashMap hashmap) {
		if (((HashMap) hashmap.get(Integer.valueOf(i))).keySet().contains(s))
			return ((Double) ((HashMap) hashmap.get(Integer.valueOf(i))).get(s))
					.doubleValue();
		else
			return 0.0D;
	}

}

