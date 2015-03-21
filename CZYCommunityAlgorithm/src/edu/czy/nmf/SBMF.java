package edu.czy.nmf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.factorization.GraphNMF;
import edu.czy.load.LoadGML;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * 
 */
public class SBMF {
	private double[][] adjMatrix;
	private SparseGraph<Vertex,Edge> graph;
	private Map<Integer,Vertex> nodeMap;
	private Map<Vertex,Integer> Mapnode;
	public SBMF(SparseGraph<Vertex,Edge> graph){
		this.graph = graph;
		this.nodeMap = new HashMap<Integer,Vertex>();
		this.Mapnode = new HashMap<Vertex,Integer>();
		int nodeCount = this.graph.getVertexCount();
		for(Vertex v:this.graph.getVertices()) {
			this.nodeMap.put(nodeCount, v);
			this.Mapnode.put(v,nodeCount);
			--nodeCount;
		}
		//build the adjMatrix
		nodeCount = this.graph.getVertexCount();
		this.adjMatrix = new double[nodeCount][nodeCount];
		//init all element to zero;
		for(int i=0;i<nodeCount;i++)
			for(int j=0;j<nodeCount;j++)
				adjMatrix[i][j]=0.0;
		for(Vertex v:this.graph.getVertices()) {
			for(Vertex neighV:this.graph.getNeighbors(v)) {
				adjMatrix[this.Mapnode.get(v)-1][this.Mapnode.get(neighV)-1]=this.graph.findEdge(v, neighV).getWeight();
			}
		}
	}
	
	public Collection<Collection<Vertex>> run(int k,int iteration,int iterationError) {
		//Step 1: nmf 
		GraphNMF nmf = new GraphNMF(this.adjMatrix,k,iteration,iterationError);
		nmf.trainSymmetric(true);
		//Step 2: get throld u
		double[][] U = nmf.getU().getArrayCopy();
		double maxElement = 0.0;
		for(int i=0;i<U.length;i++) {
			for(int j=0;j<U[i].length;j++) {
				if(U[i][j]> maxElement)
					maxElement = U[i][j];
			}
		}
		nmf.PrintMatrix(nmf.getU());
		int nodecount = U.length;
		double miniError = Double.MAX_VALUE;
		double perfectU = 0.0;
		for(double u=0.0;u<=maxElement;u=u+0.001) {
			double error = 0;
			int bigger_than_u = 0;
			Matrix UU = Matrix.constructWithCopy(U);
			UU.minusEquals(new Matrix(nodecount,k,u));
//			nmf.PrintMatrix(UU);
			for(int i=0;i<UU.getRowDimension();i++) {
				int bigger = 0;
				for(int j=0;j<UU.getColumnDimension();j++) {
					if(UU.get(i, j)> 0.0) {
						UU.set(i, j, 1.0);
						bigger += 1;
					}
					else {
						UU.set(i, j, 0.0);
					}
					
				}
				if(bigger==0)
					bigger_than_u += 1;
			}
			double[][] uu = UU.times(UU.transpose()).getArray();
			for(int j=0;j<uu.length;j++) {
				double columnSum = 0.0;
				for(int i=0;i<uu[j].length;i++) {
					if(uu[i][j]>1.0)
						uu[i][j]=1.0;
					columnSum += Math.abs(adjMatrix[i][j]-uu[i][j]);
				}
				if(columnSum> error)
					error = columnSum;
			}
			error = error+bigger_than_u;
			System.out.println("u= "+u+" Error= "+error);
			if(error < miniError) {
				miniError = error;
				perfectU = u;
			}
		}
		//Step 3 : get community structure according to 
		System.out.println("Maximum u= "+perfectU);
		List<Collection<Vertex>> results = new ArrayList<Collection<Vertex>>();
		for(int i=0;i<k;i++) {
			results.add(new ArrayList<Vertex>());
		}
		for(int i=0;i<U.length;i++) {
			for(int j=0;j<U[i].length;j++) {
				if(U[i][j]-perfectU>0)
					results.get(j).add(this.nodeMap.get(i+1));
			}
		}
		for(int i=k-1;i>=0;i--) {
			if(results.get(i).size() <= 0 ) {
				results.remove(i);
			}
		}
		return results;
	}
	
	public static void main(String[] args) {
		String gmlfilename="J:\\paperproject\\DataSet\\karate\\karate.gml";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> graph=loadGML.loadGraph(gmlfilename);
		SBMF sbmf = new SBMF(graph);
		Collection<Collection<Vertex>> coms = sbmf.run(2,100,5);
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q = MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularity Q = "+Q);
	}
}
