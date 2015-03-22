package edu.czy.measure;

import java.util.Collection;

/*
An implementation of a Normalized Mutual Information (NMI) measure for sets of overlapping clusters.

Fully described in:
   "Normalized Mutual Information to evaluate overlapping community finding algorithms 2013"
   by Aaron F. McDaid, Derek Greene, Neil Hurley
   http://arxiv.org/abs/1110.2515

Our method is based on the method described in Appendix B at the end of:
  "Detecting the overlapping and hierarchical community structure in complex networks"
  by Andrea Lancichinetti, Santo Fortunato and János Kertész
  http://iopscience.iop.org/1367-2630/11/3/033015/
 
 */
public class OverlapNMI {
	public static double NMIPartitionMax(Collection<Collection<Integer>> partitionFound,
			Collection<Collection<Integer>> partitionTruth,int nodeCount) {
		int[][] XY = new int[partitionTruth.size()][partitionFound.size()];
		int[] X = new int[partitionTruth.size()];
		int[] Y = new int[partitionFound.size()];
		int i = 0;
		int j = 0;
		for (Collection<Integer> com1 : partitionTruth) {
			j = 0;
			for (Collection<Integer> com2 : partitionFound) {
				XY[i][j] = intersect(com1, com2);
				X[i] += XY[i][j];
				Y[j] += XY[i][j];
				j++;
			}
			i++;
		}
		int N = nodeCount;
		double Hx = 0;
		double Hy = 0;
		//calculate the H(X) and H(Y)
		for (i = 0; i < partitionTruth.size(); i++) {
			if (X[i] > 0)
				Hx += h(X[i],N )+h(N-X[i],N);
		}
		for (j = 0; j < partitionFound.size(); j++) {
			if (Y[j] > 0)
				Hy += h(Y[j],N )+h(N-Y[j],N);
		}
		//calculate the H(X|Y) and H(Y|x)
		double Hx_y = 0.0;
		double Hy_x = 0.0;
		double Ha,Hb,Hc,Hd,Hxi_yj;
		//H(X|Y)
		for (i = 0; i < partitionTruth.size(); i++) {
			double Hxi_y = Double.MAX_VALUE;
			for (j = 0; j < partitionFound.size(); j++) {
				Hxi_yj = 0.0;
				Ha = h(N-(X[i]+Y[j]-XY[i][j]),N);
				Hb = h(Y[j]-XY[i][j],N);
				Hc = h(X[i]-XY[i][j],N);
				Hd = h(XY[i][j],N);
				if((Ha+Hd)>=(Hb+Hc)){
					Hxi_yj = Ha+Hb+Hc+Hd-h(Y[j],N)-h(N-Y[j],N);
				} else {
					Hxi_yj = h(X[i],N)+h(N-X[i],N);
				}
				if(Hxi_yj < Hxi_y) {
					Hxi_y = Hxi_yj;
				}
			}
			Hx_y +=Hxi_y;
		}
		//H(Y|X)
		for (i = 0; i < partitionFound.size(); i++) {
			double Hxi_y = Double.MAX_VALUE;
			for (j = 0; j < partitionTruth.size(); j++) {
				Hxi_yj = 0.0;
				Ha = h(N-(X[i]+Y[j]-XY[i][j]),N);
				Hb = h(Y[j]-XY[i][j],N);
				Hc = h(X[i]-XY[i][j],N);
				Hd = h(XY[i][j],N);
				if((Ha+Hd)>=(Hb+Hc)){
					Hxi_yj = Ha+Hb+Hc+Hd-h(Y[j],N)-h(N-Y[j],N);
				} else {
					Hxi_yj = h(X[i],N)+h(N-X[i],N);
				}
				if(Hxi_yj < Hxi_y) {
					Hxi_y = Hxi_yj;
				}
			}
			Hy_x +=Hxi_y;
		}
		double Ixy = 0.5*(Hx+Hy-Hx_y-Hy_x);
		double InormXY = Ixy / Math.max(Hx, Hy);
		return InormXY;
	}
	public static double NMIPartitionLFK(Collection<Collection<Integer>> partitionFound,
			Collection<Collection<Integer>> partitionTruth,int nodeCount) {
		int[][] XY = new int[partitionTruth.size()][partitionFound.size()];
		int[] X = new int[partitionTruth.size()];
		int[] Y = new int[partitionFound.size()];
		int i = 0;
		int j = 0;
		for (Collection<Integer> com1 : partitionTruth) {
			j = 0;
			for (Collection<Integer> com2 : partitionFound) {
				XY[i][j] = intersect(com1, com2);
				X[i] += XY[i][j];
				Y[j] += XY[i][j];
				j++;
			}
			i++;
		}
		int N = nodeCount;
		double Hx = 0;
		double Hy = 0;
		//calculate the H(X) and H(Y)
		for (i = 0; i < partitionTruth.size(); i++) {
			if (X[i] > 0)
				Hx += h(X[i],N )+h(N-X[i],N);
		}
		for (j = 0; j < partitionFound.size(); j++) {
			if (Y[j] > 0)
				Hy += h(Y[j],N )+h(N-Y[j],N);
		}
		//calculate the H(X|Y) and H(Y|x)
		double Hx_y = 0.0;
		double Hy_x = 0.0;
		double Ha,Hb,Hc,Hd,Hxi_yj;
		//H(X|Y)
		for (i = 0; i < partitionTruth.size(); i++) {
			double Hxi_y = Double.MAX_VALUE;
			for (j = 0; j < partitionFound.size(); j++) {
				Hxi_yj = 0.0;
				Ha = h(N-(X[i]+Y[j]-XY[i][j]),N);
				Hb = h(Y[j]-XY[i][j],N);
				Hc = h(X[i]-XY[i][j],N);
				Hd = h(XY[i][j],N);
				if((Ha+Hd)>=(Hb+Hc)){
					Hxi_yj = Ha+Hb+Hc+Hd-h(Y[j],N)-h(N-Y[j],N);
				} else {
					Hxi_yj = h(X[i],N)+h(N-X[i],N);
				}
				if(Hxi_yj < Hxi_y) {
					Hxi_y = Hxi_yj;
				}
			}
			Hx_y +=Hxi_y;
		}
		//H(Y|X)
		for (i = 0; i < partitionFound.size(); i++) {
			double Hxi_y = Double.MAX_VALUE;
			for (j = 0; j < partitionTruth.size(); j++) {
				Hxi_yj = 0.0;
				Ha = h(N-(X[i]+Y[j]-XY[i][j]),N);
				Hb = h(Y[j]-XY[i][j],N);
				Hc = h(X[i]-XY[i][j],N);
				Hd = h(XY[i][j],N);
				if((Ha+Hd)>=(Hb+Hc)){
					Hxi_yj = Ha+Hb+Hc+Hd-h(Y[j],N)-h(N-Y[j],N);
				} else {
					Hxi_yj = h(X[i],N)+h(N-X[i],N);
				}
				if(Hxi_yj < Hxi_y) {
					Hxi_y = Hxi_yj;
				}
			}
			Hy_x +=Hxi_y;
		}
		double Ixy = 0.5*(Hx_y/Hx+Hy_x/Hy);
		double InormXY = 1-Ixy;
		return InormXY;
	}
	private static double h(int w,int n) {
		return -w*1.0*Math.log(w*1.0/(1.0*n))/Math.log(2.0);
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
