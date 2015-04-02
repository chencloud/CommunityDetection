package edu.czy.factorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import Jama.Matrix;
import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
/**
 * 
 * @author 	CHENZHIYUN
 * @Date	2014/6/18
 */
/**
 * Realize the non-negative matrix factorization for the adj_matrix of graph
 * By the Multi Variable Update Method
 * G= U*V'
 * @version 1.0
 *
 */
public class GraphNMFNew {
	private static final String edges = null;
	private SparseGraph<Vertex,Edge> graph;
	private double[][] U;
	private int k;//the hidden feature factor
	private int itera;
	private int itera_error;
	private Map<Integer,Vertex> nodeMap;
	private int nodeCount; 
	private final double eps=Math.sqrt(2.2204e-16);
	public GraphNMFNew(SparseGraph<Vertex,Edge> g){
		this(g,null,10,100,5);
	}
	/**
	 * @param g sparsegraph
	 * @param k
	 * @param itera
	 * @param itera_error
	 *
	 */
	public GraphNMFNew(SparseGraph<Vertex,Edge> g,Map<Integer,Vertex> nodeMap, int k,int itera,int itera_error)
	{
		this.graph = g;
		this.nodeCount = this.graph.getVertexCount();
		this.k = k;
		this.itera = itera;
		this.itera_error = itera_error;
		int nodeCount = this.graph.getVertexCount();
		if(null == nodeMap) {
			this.nodeMap = new HashMap<Integer,Vertex>();
			for(Vertex v : this.graph.getVertices()) {
				this.nodeMap.put(nodeCount, v);
				v.nodeInteger = nodeCount;
				--nodeCount;
			}
		} else {
			this.nodeMap = nodeMap;
			//这里假设node.nodeInteger 已经初始化
			for(Entry<Integer,Vertex> node : nodeMap.entrySet()) {
				node.getValue().nodeInteger = node.getKey();
			}
		}
	}
	

	public void trainSymmetric(boolean isnormal){
		double[][] tempU;
		double Error=Double.MAX_VALUE;
		//this.PrintMatrix(this.adj_matrix);
		for(int error_i=0; error_i<this.itera_error; error_i++)
		{
			/*
			 * init the U、V matrix
			 */
			tempU = Matrix.random(nodeCount, k).getArray();
			/*
			 * Normalize
			 */
			if(isnormal)
			{
				for(int irow=0;irow<tempU.length;irow++)
				{
					double irowsum=0.0;
					for(int jcol=0;jcol<tempU[irow].length;jcol++)
					{
						irowsum+=tempU[irow][jcol];
					}
					for(int jcol=0;jcol<tempU[irow].length;jcol++)
					{
						tempU[irow][jcol] /= (irowsum+this.eps);
					}
				}
			}
			System.out.println("开始运行.....");
			double error = Double.MAX_VALUE;
			for(int i=0;i<this.itera;i++)//iteration number
			{
				/**
				 * Update U
				 */
				double[][] olderU = tempU;
//				double[][] temp1 = AdjTimesU(olderU);
////				this.PrintMatrix(temp1);
//				double[][] temp2 = UTimesUTransposeTimesU(olderU);
//				double[][] newU = UpateU(olderU,temp1,temp2);
				double[][] newU = UpdateU(olderU);
				if(isnormal)
				{
					for(int irow=0;irow<newU.length;irow++)
					{
						double irowsum=0.0;
						for(int jcol=0;jcol<newU[irow].length;jcol++)
						{
							irowsum+=newU[irow][jcol];
						}
						for(int jcol=0;jcol<newU[irow].length;jcol++)
						{
							newU[irow][jcol] = newU[irow][jcol]/(irowsum+this.eps);
						}
					}
				}			
				double curError =calcluteError(newU);
//				System.out.println("the curiteraion curError="+curError);
//				if(error > curError) {
//					error = curError;
//				} else {
//					break;
//				}
				tempU = newU;
			}
			
			/*
			 * estimate the error
			 */
			double curError = calcluteError(tempU);
			System.out.println("the curiteraion minimun curError="+curError);
			if(curError<Error){				
				Error=curError;				
				this.U= tempU;
			}
		}
		System.out.println("the final minimun curError="+Error);
	}
	public void trainSymmetricMultiArray(boolean isnormal){
		double[][] tempU;
		double Error=Double.MAX_VALUE;
		//this.PrintMatrix(this.adj_matrix);
		for(int error_i=0; error_i<this.itera_error; error_i++)
		{
			/*
			 * init the U、V matrix
			 */
			tempU = Matrix.random(nodeCount, k).getArray();
			/*
			 * Normalize
			 */
			if(isnormal)
			{
				for(int irow=0;irow<tempU.length;irow++)
				{
					double irowsum=0.0;
					for(int jcol=0;jcol<tempU[irow].length;jcol++)
					{
						irowsum+=tempU[irow][jcol];
					}
					for(int jcol=0;jcol<tempU[irow].length;jcol++)
					{
						tempU[irow][jcol] /= (irowsum+this.eps);
					}
				}
			}
			System.out.println("开始运行.....");
			double error = Double.MAX_VALUE;
			for(int i=0;i<this.itera;i++)//iteration number
			{
				/**
				 * Update U
				 */
				double[][] olderU = tempU;
				double[][] temp1 = AdjTimesU(olderU);
				System.out.println("jisuan adjtimesU");
//				this.PrintMatrix(temp1);
				double[][] temp2 = UTimesUTransposeTimesU(olderU);
				System.out.println("jisuan UTimesUTransposeTimesU");
				double[][] newU = UpateU(olderU,temp1,temp2);
				System.out.println("jisuan UpateU");
//				double[][] newU = UpdateU(olderU);
				if(isnormal)
				{
					for(int irow=0;irow<newU.length;irow++)
					{
						double irowsum=0.0;
						for(int jcol=0;jcol<newU[irow].length;jcol++)
						{
							irowsum+=newU[irow][jcol];
						}
						for(int jcol=0;jcol<newU[irow].length;jcol++)
						{
							newU[irow][jcol] = newU[irow][jcol]/(irowsum+this.eps);
						}
					}
				}			
				double curError =calcluteError(newU);
				System.out.println("the curiteraion curError="+curError);
				if(error > curError) {
					error = curError;
				} else {
					break;
				}
				tempU = newU;
			}
			
			/*
			 * estimate the error
			 */
			double curError = calcluteError(tempU);
			System.out.println("the curiteraion minimun curError="+curError);
			if(curError<Error){				
				Error=curError;				
				this.U= tempU;
			}
		}
		System.out.println("the final minimun curError="+Error);
	}
	private double[][] UpateU(double[][] olderU, double[][] temp1,
			double[][] temp3) {
		// TODO Auto-generated method stub
		for(int irow=0;irow<temp1.length;irow++)
		{
			for(int jcol=0;jcol<temp1[irow].length;jcol++)
			{
				temp1[irow][jcol] = olderU[irow][jcol]*temp1[irow][jcol]/(this.eps+temp3[irow][jcol]);
			}
		}
		return temp1;
	}
	private double[][] UpdateU(double[][] olderU) {
		double[][] AU = new double[nodeCount][k];
		double[] UUTi = new double[nodeCount];
		Set<Integer> neighSet = new HashSet<Integer>();
		for(int i=1; i<=this.graph.getVertexCount(); i++) {
			//calcualte AU-irow
			neighSet.clear();
			for(Vertex n : this.graph.getNeighbors(this.nodeMap.get(i))) {
				neighSet.add(n.nodeInteger);
			}
			for(int j=0; j<k; j++) {
				double UUT_ij = 0;
				for(int nindex = 1; nindex<=nodeCount; nindex++) {
					double av = neighSet.contains(Integer.valueOf(nindex))?1.0:0.0;
					UUT_ij += olderU[nindex-1][j]*av;
				}
				AU[i-1][j] = UUT_ij;
			}
			//calculate UUTU-irow
			for(int j=0; j<nodeCount; j++) {
				double uutij = 0.0;
				for(int kindex=0; kindex<k; kindex++) {
					uutij += olderU[i-1][kindex]*olderU[j][kindex];
				}
				UUTi[j] = uutij;
			}
			for(int kindex=0; kindex<k; kindex++) {
				double uutuik = 0.0;
				for(int j=0; j<nodeCount; j++){
					uutuik +=UUTi[j]*olderU[j][kindex];
				}
				AU[i-1][kindex] = olderU[i-1][kindex]*AU[i-1][kindex]/uutuik;
			}	
		}
		return AU;
	}
	private double[][] UTimesUTransposeTimesU(double[][] olderU) {
		// TODO Auto-generated method stub
		double[][] UUTU =new double[nodeCount][k];
		double[] UUTi = new double[nodeCount];
		for(int i=0; i<nodeCount; i++) {
			
			for(int j=0; j<nodeCount; j++) {
				double uutij = 0.0;
				for(int kindex=0; kindex<k; kindex++) {
					uutij += olderU[i][kindex]*olderU[j][kindex];
				}
				UUTi[j] = uutij;
			}
			for(int kindex=0; kindex<k; kindex++) {
				double uutuik = 0.0;
				for(int j=0; j<nodeCount; j++){
					uutuik +=UUTi[j]*olderU[j][kindex];
				}
				UUTU[i][kindex] = uutuik;
			}
		}
		return UUTU;
	}
	private double[][] AdjTimesU(double[][] olderU) {
		// TODO Auto-generated method stub
		double[][] AU = new double[nodeCount][k];
		Set<Integer> neighSet = new HashSet<Integer>();
		for(int i=1; i<=this.graph.getVertexCount(); i++) {
			neighSet.clear();
			for(Vertex n : this.graph.getNeighbors(this.nodeMap.get(i))) {
				neighSet.add(n.nodeInteger);
			}
			for(int j=0; j<k; j++) {
				double UUT_ij = 0;
				for(int nindex = 1; nindex<=nodeCount; nindex++) {
					double av = neighSet.contains(Integer.valueOf(nindex))?1.0:0.0;
					UUT_ij += olderU[nindex-1][j]*av;
				}
				AU[i-1][j] = UUT_ij;
			}
		}
		return AU;
	}
	private double calcluteError(double[][] newU) {
		// TODO Auto-generated method stub
		double sumError = 0;
		Set<Integer> neighSet = new HashSet<Integer>();
		for(int i=1; i<=this.graph.getVertexCount(); i++) {
			neighSet.clear();
			for(Vertex n : this.graph.getNeighbors(this.nodeMap.get(i))) {
				neighSet.add(n.nodeInteger);
			}
			for(int j=1; j<=this.graph.getVertexCount(); j++) {
				double av = neighSet.contains(Integer.valueOf(j))?1.0:0.0;
				double UUT_ij = 0;
				for(int kindex = 0; kindex<k; kindex++) {
					UUT_ij += newU[i-1][kindex]*newU[j-1][kindex];
				}
				sumError +=(av-UUT_ij)*(av-UUT_ij);
			}
		}
		return sumError;
	}
	public void PrintMatrix(Matrix m) {
		for(int i=0;i<m.getRowDimension();i++) {
			for(int j=0;j<m.getColumnDimension();j++) {
				System.out.print(m.get(i, j)+"\t");
			}
			System.out.println();
		}
	}
	public void PrintMatrix(double[][] m) {
		for(int i=0;i<m.length;i++) {
			for(int j=0;j<m[i].length;j++) {
				System.out.print(m[i][j]+"\t");
			}
			System.out.println();
		}
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public int getItera() {
		return itera;
	}
	public void setItera(int itera) {
		this.itera = itera;
	}
	public int getItera_error() {
		return itera_error;
	}
	public void setItera_error(int itera_error) {
		this.itera_error = itera_error;
	}
	public double getEps() {
		return eps;
	}
	public double[][] getU() {
		return U;
	}
	public void setU(double[][] u) {
		U = u;
	}
	public void initVertexWeight() {
		for(int i=0;i<U.length;i++) {
			Vertex v = this.nodeMap.get(i+1);
			List<Double> ws = new ArrayList<Double>();
			for(int j=1;j<U[i].length;j++)
				ws.add(U[i][j]);
			v.setWeight(ws);
		}
	}
}