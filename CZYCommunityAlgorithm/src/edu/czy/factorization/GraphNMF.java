package edu.czy.factorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class GraphNMF {
	private static final String edges = null;
	private SparseGraph<Vertex,Edge> sparse_graph;
	private Matrix adj_matrix;
	private Matrix U;
	private Matrix V;
	public Matrix getAdj_matrix() {
		return adj_matrix;
	}
	public void setAdj_matrix(Matrix adj_matrix) {
		this.adj_matrix = adj_matrix;
	}
	public Matrix getU() {
		return U;
	}
	public void setU(Matrix u) {
		U = u;
	}
	public Matrix getV() {
		return V;
	}
	public void setV(Matrix v) {
		V = v;
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
	private int k;//the hidden feature factor
	private int itera;
	private int itera_error;
	private Map<Vertex,Integer> NodeIdMap;
	private final double eps=Math.sqrt(2.2204e-16);
	
	public GraphNMF(SparseGraph<Vertex,Edge> g){
		this(g,10,100,5);
	}
	public GraphNMF(double[][] adjM,int k,int iteration,int iterationError) {
		this.adj_matrix = new Matrix(adjM);
		this.k = k;
		this.itera = iteration;
		this.itera_error = iterationError;
	}
	/**
	 * @param g sparsegraph
	 * @param k
	 * @param itera
	 * @param itera_error
	 *
	 */
	public GraphNMF(SparseGraph<Vertex,Edge> g,int k,int itera,int itera_error)
	{
		this.sparse_graph=g;
		this.k=k;
		this.itera=itera;
		this.itera_error=itera_error;
	}
	
	/*
	 * Init the adjMatrix,and U 、V Matrix
	 */
	public void InitParameter(){
		this.NodeIdMap=new HashMap<Vertex,Integer>();
		int initcount=0;
		int nodecount=this.sparse_graph.getVertexCount();
		/*
		 * init the adj matrix
		 */
		this.adj_matrix=new Matrix(nodecount,nodecount);
		Collection<Edge> edges=this.sparse_graph.getEdges();
		Vertex sV,tV;
		int sid,tid;
		for(Edge e:edges)
		{
			Pair<Vertex> pPair=this.sparse_graph.getEndpoints(e);
			sV=pPair.getFirst();
			tV=pPair.getSecond();
			if(!NodeIdMap.containsKey(sV))
			{
				NodeIdMap.put(sV, initcount++);
			}
			if(!NodeIdMap.containsKey(tV))
			{
				NodeIdMap.put(tV, initcount++);
			}
			sid=NodeIdMap.get(sV);
			tid=NodeIdMap.get(tV);
			this.adj_matrix.set((int)sid,(int) tid, 1.0);
		}
		System.out.println("the initcount:"+initcount);
		
	}
	/**
	 * train the NMF MODEL by multi variable method
	 */
	public void trainNonSymmetric(boolean isnormal){
		Matrix tempU,tempV;
		double Error=Double.MAX_VALUE;
		for(int error_i=0;error_i<this.itera_error;error_i++)
		{
			/*
			 * init the U、V matrix
			 */
			int nodecount=this.sparse_graph.getVertexCount();
			tempU=Matrix.random(nodecount, k);
			tempV=Matrix.random(k, nodecount);
			
			for(int i=0;i<this.itera;i++)//iteration number
			{
				/**
				 * Update U
				 */
				Matrix temp1=tempU.times(this.adj_matrix.times(tempV.transpose()));
				Matrix temp2=tempU.times(tempV).times(tempV.transpose());
				temp1.arrayRightDivideEquals(temp2.plus(new Matrix(nodecount,k,0.00001)));
				tempU =temp1;
				/*
				 * Normalize
				 */
				if(isnormal)
				{
					for(int irow=0;irow<tempU.getRowDimension();irow++)
					{
						double irowsum=0.0;
						for(int jcol=0;jcol<tempU.getColumnDimension();jcol++)
						{
							irowsum+=tempU.get(irow, jcol);
						}
						for(int jcol=0;jcol<tempU.getColumnDimension();jcol++)
						{
							tempU.set(irow, jcol, tempU.get(irow, jcol)/(irowsum+0.00001));
						}
					}
				}
				/**
				 * 
				 * Update V
				 */
				temp1=tempU.transpose().times(this.adj_matrix);
				temp2=tempU.transpose().times(tempU).times(tempV);
				temp1.arrayRightDivideEquals(temp2);
				tempV.arrayTimesEquals(temp1);
				if(isnormal)
				{
					for(int irow=0;irow<tempV.getRowDimension();irow++)
					{
						double irowsum=0.0;
						for(int jcol=0;jcol<tempV.getColumnDimension();jcol++)
						{
							irowsum+=tempV.get(irow, jcol);
							//System.out.println(irow+","+jcol+":"+tempV.get(irow, jcol));
						}
						
						for(int jcol=0;jcol<tempV.getColumnDimension();jcol++)
						{
							tempV.set(irow, jcol, tempV.get(irow, jcol)/(irowsum));
						}
					}
				}
			}
			/*
			 * estimate the error
			 */
			Matrix errorMatrix=this.adj_matrix.minus(tempU.times(tempV));
			errorMatrix.arrayTimesEquals(errorMatrix);
			double curError=0.0;
			for(int row=0;row<errorMatrix.getRowDimension();row++)
				for(int col=0;col<errorMatrix.getColumnDimension();col++)
					curError+=errorMatrix.get(row, col);
			System.out.println("curError="+curError);
			if(curError<Error){
				Error=curError;
//				System.out.println("Error="+Error);
				this.U=tempU;
				this.V=tempV;
			}
		}
	}
	public void trainSymmetric(boolean isnormal){
		Matrix tempU,tempV;
		double Error=Double.MAX_VALUE;
		//this.PrintMatrix(this.adj_matrix);
		for(int error_i=0;error_i<this.itera_error;error_i++)
		{
			/*
			 * init the U、V matrix
			 */
			int nodecount= this.adj_matrix.getRowDimension();
			tempU=Matrix.random(nodecount, k);
			/*
			 * Normalize
			 */
			if(isnormal)
			{
				for(int irow=0;irow<tempU.getRowDimension();irow++)
				{
					double irowsum=0.0;
					for(int jcol=0;jcol<tempU.getColumnDimension();jcol++)
					{
						irowsum+=tempU.get(irow, jcol);
					}
					for(int jcol=0;jcol<tempU.getColumnDimension();jcol++)
					{
						tempU.set(irow, jcol, tempU.get(irow, jcol)/(irowsum+0.00001));
					}
				}
			}
			tempV=tempU.transpose();
			double error = Double.MAX_VALUE;
			for(int i=0;i<this.itera;i++)//iteration number
			{
				/**
				 * Update U
				 */
				Matrix temp1=tempU.arrayTimes((this.adj_matrix.times(tempV.transpose())));
				Matrix temp2=tempU.times(tempV).times(tempV.transpose());
				temp1.arrayRightDivideEquals(temp2.plus(new Matrix(nodecount,k,eps)));
//				this.PrintMatrix(temp1);
				Matrix ttempU = temp1.plus(new Matrix(nodecount,k,eps));
				
				/*
				 * Normalize
				 */
				if(isnormal)
				{
					for(int irow=0;irow<ttempU.getRowDimension();irow++)
					{
						double irowsum=0.0;
						for(int jcol=0;jcol<ttempU.getColumnDimension();jcol++)
						{
							irowsum+=ttempU.get(irow, jcol);
						}
						for(int jcol=0;jcol<ttempU.getColumnDimension();jcol++)
						{
							ttempU.set(irow, jcol, ttempU.get(irow, jcol)/(irowsum));
						}
					}
				}
				
				/*
				 * estimate the error
				 */
				Matrix errorMatrix=this.adj_matrix.minus(ttempU.times(tempU.transpose()));
				errorMatrix.arrayTimesEquals(errorMatrix);
				double curError=0.0;
				for(int row=0;row<errorMatrix.getRowDimension();row++)
					for(int col=0;col<errorMatrix.getColumnDimension();col++)
						curError+=errorMatrix.get(row, col);
				tempU = ttempU;
				tempV = tempU.transpose();

			}
			
			/*
			 * estimate the error
			 */
			Matrix errorMatrix=this.adj_matrix.minus(tempU.times(tempV));
			errorMatrix.arrayTimesEquals(errorMatrix);
			double curError=0.0;
			for(int row=0;row<errorMatrix.getRowDimension();row++)
				for(int col=0;col<errorMatrix.getColumnDimension();col++)
					curError+=errorMatrix.get(row, col);
			System.out.println("the"+error_i+" iteration curError="+curError);
			if(curError<Error){				
				Error=curError;				
				this.U=tempU;
				this.V=tempV;
			}
		}
		System.out.println("the final minimun curError="+Error);
	}
	/*
	 * Update Vertex's weight
	 */
	public SparseGraph<Vertex,Edge> UpdateNodesWeight(){
		for(Map.Entry<Vertex, Integer> e:NodeIdMap.entrySet()){
			Vertex v=e.getKey();
			int vid=e.getValue();
			List<Double> vWeights=new ArrayList<Double>();
			//System.out.println(v.getId());
			for(int i=0;i<k;i++){
				vWeights.add(this.U.get(vid, i));
				//System.out.print(this.U.get(vid, i)+";");
			}
			v.setWeight(vWeights);
		}
		return this.sparse_graph;
	}
	public void PrintMatrix(Matrix m) {
		for(int i=0;i<m.getRowDimension();i++) {
			for(int j=0;j<m.getColumnDimension();j++) {
				System.out.print(m.get(i, j)+"\t");
			}
			System.out.println();
		}
	}
}
