package edu.czy.nmf;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Jama.Matrix;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.factorization.GraphNMF;
import edu.czy.factorization.GraphNMFNew;
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
		GraphNMFNew nmf = new GraphNMFNew(this.graph,null,k,iteration,iterationError);
		nmf.trainSymmetricMultiArray(true);
		//Step 2: get throld u
		double[][] U = nmf.getU();
		double maxElement = 0.0;
		for(int i=0;i<U.length;i++) {
			for(int j=0;j<U[i].length;j++) {
				if(U[i][j]> maxElement)
					maxElement = U[i][j];
			}
		}
//		nmf.PrintMatrix(nmf.getU());
		int nodecount = U.length;
		double miniError = Double.MAX_VALUE;
		double perfectU = 0.0;
		for(double u=0.0;u<=maxElement;u=u+0.01) {
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
			double maxU = 0.0;
			int index = 0;
			for(int j=0;j<U[i].length;j++) {
				if(U[i][j]-maxU>0){
					maxU = U[i][j];
					index = j;
				}
			}
			if(maxU-perfectU >0) {
				results.get(index).add(this.nodeMap.get(i+1));
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
		String basedir = "E:\\dataset\\unweight_dataset\\";
		String[] truthNewworkFilenames = {
				"karate\\karate.gml",
				"dolphins\\dolphins.gml",
				"football\\football.gml",
				"polbooks\\polbooks.gml",
				"jazz\\jazz.net",
				"mexican\\mexican.net",
				"adjnoun\\adjnoun.gml",
				"celegans_metabolic\\celegans_metabolic.net",
				"email\\email.net",
				"pro-pro\\pro-pro.net",
				"power_grid\\power_grid.gml",
				"pgp\\pgp.net",
				"Internet\\Internet.gml",
				"LFKnetwork\\100.gml",
				"LFKnetwork\\500.gml",
				"LFKnetwork\\1000.gml",
				"LFKnetwork\\5000.gml",
				"LFKnetwork\\10000.gml",
				"LFKnetwork\\50000.gml",
				"LFKnetwork\\100000.gml",
				"LFKnetwork\\500000.gml",
				"LFKnetwork\\1000000.gml"
		};
		int lengths = truthNewworkFilenames.length;
//		String storeFilename = "G:\\2015硕士毕业论文资料\\实验结果\\SNMF.txt";
		PrintStream ps = null;
		try {
			ps = new PrintStream(System.out);
			int i = 17;
//			for(;i< lengths; i++) 
			{
				String filename = truthNewworkFilenames[i];
				int k = 2;
				SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(basedir+filename);
				Collection<Collection<Integer>> partitionTrue = GraphUtils.exportCommunityGroundTruthCollection(graph);
//				//找到所有的极大点
//				Set<Long> localnodeSet = new HashSet<Long>();
//				k = 0;
//				for(Vertex v:graph.getVertices()) {
//					boolean isLocalCenter = true;
//					for(Vertex neighV:graph.getNeighbors(v)) {
//						if(graph.degree(neighV)>graph.degree(v)) {
//							isLocalCenter = false;
//						} 
//						else if(graph.degree(neighV)== graph.degree(v)&&localnodeSet.contains(neighV.getId())) {
//							isLocalCenter = false;
//						}
//					}
//					if(isLocalCenter) {
//						k += 1;
//						localnodeSet.add(v.getId());
//					}
//				}
//				if( k<=1 )k = 2;
//				System.out.println(k);
				k = partitionTrue.size(); 
				SBMF sbmf = new SBMF(graph);
				Collection<Collection<Vertex>> coms = sbmf.run(k,100,1);
//				GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
				ps.println("==============================================");
				ps.println(filename);
				ps.println("k="+k);
				double Q = MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
				ps.println("Modularity Q = "+Q);
				ps.println("==============================================");
				ps.println("==============================================");
				Collection<Collection<Integer>> partition = new ArrayList<Collection<Integer>>();
				for(Collection<Vertex> com : coms) {
					Collection<Integer> p = new ArrayList<Integer>();
					for(Vertex v: com) {
						p.add((int)v.getId());
					}
					partition.add(p);
				}
				
				double NMI = MeasureCollections.calculateNMI(partition, partitionTrue, graph.getVertexCount());
				ps.println("NMI ="+NMI);
				ps.println("GrouthTrueth Q="+MeasureCollections.calculateQFromCollectionsForTruth(graph, partitionTrue));
				ps.flush();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			if(null != ps)ps.close();
		}
	}
}
