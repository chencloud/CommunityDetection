package edu.czy.nmf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWCellArray;
import BayesNMF.BayesNMF;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.load.LoadGML;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 *调用matlab实现的BayesNMF 
 */
public class BayesNMFWrapper {
	private double[][] adjMatrix;
	private SparseGraph<Vertex,Edge> graph;
	private Map<Integer,Vertex> nodeMap;
	private Map<Vertex,Integer> Mapnode;
	public BayesNMFWrapper(SparseGraph<Vertex,Edge> graph){
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
//		for(int i=0;i<nodeCount;i++)
//				adjMatrix[i][i]=this.graph.degree(this.nodeMap.get(i+1));
	}
	
	public Collection<Collection<Vertex>> run(){
		MWNumericArray adjV = null;
		BayesNMF bnmf = null;
		
		try {
			
			int nodeCount = this.graph.getVertexCount();
			bnmf = new BayesNMF();
			Object[] input = new Object[1];
			Object[] output = new Object[4];
			adjV = new MWNumericArray(this.adjMatrix, MWClassID.SINGLE);
			input[0] = adjV;
			bnmf.commDetNMF(output, input);
			MWCellArray groups = (MWCellArray)output[1];
			List<MWArray> comms = groups.asList();
//			System.out.println(comms.size());
			Collection<Collection<Vertex>> results = new ArrayList<Collection<Vertex>>();
			for(MWArray com : comms) {
				Collection<Vertex> result = new ArrayList<Vertex>();
				int count = com.getDimensions()[1];
				for(int i=1;i<=count;i++) {
					result.add(this.nodeMap.get(((Double)com.get(i)).intValue()));
				}
				results.add(result);
			}
			return results;
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			MWNumericArray.disposeArray(adjV);
			if(null != bnmf) {
				bnmf.dispose();
			}
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		String gmlfilename="J:\\paperproject\\DataSet\\karate\\karate.gml";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> graph=loadGML.loadGraph(gmlfilename);
		BayesNMFWrapper bnmfw = new BayesNMFWrapper(graph);
		Collection<Collection<Vertex>> coms = bnmfw.run();
		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
		double Q = MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
		System.out.println("Modularity Q = "+Q);
	}
}
