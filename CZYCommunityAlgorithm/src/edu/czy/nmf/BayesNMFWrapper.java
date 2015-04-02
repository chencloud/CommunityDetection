package edu.czy.nmf;

import java.io.FileOutputStream;
import java.io.PrintStream;
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
		String storeFilename = "G:\\2015硕士毕业论文资料\\实验结果\\BayesNMF.txt";
		PrintStream ps = null;
		try {
//			ps = new PrintStream(new FileOutputStream(storeFilename));
			ps = new PrintStream(System.out);
			int i=16;
//			for(;i< lengths; i++) 
			{
				SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(basedir+truthNewworkFilenames[i]);
				BayesNMFWrapper bnmfw = new BayesNMFWrapper(graph);
				Collection<Collection<Vertex>> coms = bnmfw.run();
	//			GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
				ps.println("==============================================");
				ps.println(truthNewworkFilenames[i]);
				double Q = MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
				ps.println("Modularity Q = "+Q);
				ps.println("==============================================");
				Collection<Collection<Integer>> partition = new ArrayList<Collection<Integer>>();
				for(Collection<Vertex> com : coms) {
					Collection<Integer> p = new ArrayList<Integer>();
					for(Vertex v: com) {
						p.add((int)v.getId());
					}
					partition.add(p);
				}
				Collection<Collection<Integer>> partitionTrue = GraphUtils.exportCommunityGroundTruthCollection(graph);
				double NMI = MeasureCollections.calculateNMI(partition, partitionTrue, graph.getVertexCount());
				ps.println("NMI ="+NMI);
				ps.println("GrouthTrueth Q="+MeasureCollections.calculateQFromCollectionsForTruth(graph, partitionTrue));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally{
			if(null != ps)ps.close();
		}
	}
	/*
	 * 
==============================================
football\football.gml
Modularity Q = 0.603324116678066
==============================================
==============================================
LFKnetwork\100.gml
Modularity Q = 0.4701975666927124
==============================================
==============================================
LFKnetwork\5000.gml
k=100
Modularity Q = 0.309729812725333
==============================================
==============================================
NMI =0.5704334405976921
GrouthTrueth Q=0.882365989576837
	 */
}
