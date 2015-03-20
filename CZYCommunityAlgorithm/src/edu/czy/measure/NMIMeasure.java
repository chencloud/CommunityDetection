package edu.czy.measure;

import java.util.Collection;

public class NMIMeasure {
	public static double NMIPartition1(Collection<Collection<Integer>> partitionF,
			Collection<Collection<Integer>> partitionR,int nodeCount) {
		int[][] XY = new int[partitionR.size()][partitionF.size()];
		int[] X = new int[partitionR.size()];
		int[] Y = new int[partitionF.size()];
		int i = 0;
		int j = 0;
		for (Collection<Integer> com1 : partitionR) {
			j = 0;
			for (Collection<Integer> com2 : partitionF) {
				XY[i][j] = intersect(com1, com2);
				X[i] += XY[i][j];
				Y[j] += XY[i][j];
				j++;
			}
			i++;
		}
		int N = nodeCount;
		double Ixy = 0;
		for (i = 0; i < partitionR.size(); i++) {
			for (j = 0; j < partitionF.size(); j++) {
				if (XY[i][j] > 0) {
					Ixy += ((double) XY[i][j] / N)
							* (Math.log((double) XY[i][j] * N / (X[i] * Y[j])) / Math
									.log(2.0));
				}
			}
		}
		double Hx = 0;
		double Hy = 0;
		for (i = 0; i < partitionR.size(); i++) {
			if (X[i] > 0)
				Hx += h((double) X[i] / N);
		}
		for (j = 0; j < partitionF.size(); j++) {
			if (Y[j] > 0)
				Hy += h((double) Y[j] / N);
		}
		double InormXY = 2 * Ixy / (Hx + Hy);
		return InormXY;
	}
	
	private static double h(double p) {
		return -p * (Math.log(p) / Math.log(2.0));
	}
	private static int intersect(Collection<Integer> com1, Collection<Integer> com2) {
		int num = 0;
		for (Integer v1 : com1) {
			if (com2.contains(v1))
				num++;
		}
		return num;
	}
}
