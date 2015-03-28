package edu.czy.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.export.ExportFile;
import edu.czy.factorization.GraphNMF;
import edu.czy.importance.NodeImportance;
import edu.czy.load.LoadEdgeFile;
import edu.czy.load.LoadGML;
import edu.czy.load.LoadGroundTruthFile;
import edu.czy.lpa.LPA;
import edu.czy.lpa.LPAM;
import edu.czy.lpa.NMFWLPA;
import edu.czy.lpa.StandardLPA;
import edu.czy.measure.MeasureCollections;
import edu.czy.measure.Measure;
import edu.czy.utils.GraphStatistic;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public class MainDriver {
	public static void repeatRun(SparseGraph<Vertex,Edge> graph,int repeatNum,boolean isQ,boolean isNMI, PrintStream pw)  {
		double[] valuesQ = null;
		double maxQ = Double.MIN_VALUE;
		double minQ = Double.MAX_VALUE;	
		if(isQ)valuesQ = new double[repeatNum];
		double[] valuesNMI = null;
		double maxNMI = Double.MIN_VALUE;
		double minNMI = Double.MAX_VALUE;
		if(isNMI)valuesNMI = new double[repeatNum];
		for(int i=0; i<repeatNum; i++) {
			//run algorithm;set algorithm
//			LPA nmfwlpa = new NMFWLPA(graph,1000,true,true);
//			nmfwlpa.run();
//			LPA standardlpa = new StandardLPA(graph,1000);
//			standardlpa.run();
			LPA lpam = new LPAM(graph,1000);
			lpam.run();
			if(isQ){
				valuesQ[i] = MeasureCollections.calculateQFromCollectionsWithVertex(graph, GraphUtils.exportCommunityCollectionWithVertex(graph));
				if(valuesQ[i]>maxQ) maxQ = valuesQ[i];
				if(valuesQ[i]<minQ) minQ = valuesQ[i];
			}
			if(isNMI){
				Collection<Collection<Integer>> partitionF = GraphUtils.exportCommunityCollection(graph);
				Collection<Collection<Integer>> partitionR = GraphUtils.exportCommunityGroundTruthCollection(graph);
//				GraphUtils.PrintCommunityCollections(partitionF, ",");
//				GraphUtils.PrintCommunityCollections(partitionR, ";");
				valuesNMI[i] = MeasureCollections.calculateNMI(partitionF ,partitionR,graph.getVertexCount());
				if(valuesNMI[i]>maxNMI) maxNMI = valuesNMI[i];
				if(valuesNMI[i]<minNMI) minNMI = valuesNMI[i];
			}
		}
		//get statistic information
		pw.println("******************************************");
		pw.println("N="+graph.getVertexCount()+";E="+graph.getEdgeCount());
		pw.println("RepeatNum="+repeatNum);
		pw.println("******************************************");
		if(isQ) {
			pw.println("Modularity Q Statistic:");
			pw.println("MaxQ="+maxQ+";MinQ="+minQ);
			double[] result = GraphStatistic.calculateAvgStd(valuesQ);
			pw.println("AvgQ="+result[0]+";StdQ="+result[1]);
		}
		if(isNMI) {
			pw.println("NMI Statistic:");
			pw.println("MaxNMI="+maxNMI+";MinNMI="+minNMI);
			double[] result = GraphStatistic.calculateAvgStd(valuesNMI);
			pw.println("AvgNMI="+result[0]+";StdNMI="+result[1]);
		}
	}
	
	public static void RunMultiFile(String basedir,String[] filename,PrintStream pw) {
		for(int i=0;i<filename.length;i++) {
			pw.println("++++++++++++++++++++++++++++++++++++++++++++++++");
			pw.println("Running "+filename[i]);
			pw.println("++++++++++++++++++++++++++++++++++++++++++++++++");
			SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(basedir+filename[i]);
			pw.println("N="+graph.getVertexCount()+";E="+graph.getEdgeCount());
			//set parameter;
			repeatRun(graph,10,true,false,pw);
			pw.println("++++++++++++++++++++++++++++++++++++++++++++++++\n");
		}
	}
	public static void main(String[] args)
	{
		String basedir = "E:\\dataset\\unweight_dataset\\";
		String[] truthNewworkFilenames = {
				"karate\\karate.gml",
				"dolphins\\dolphins.gml",
				"Internet\\Internet.gml",
				"polbooks\\polbooks.gml",
				"jazz\\jazz.net",
				"email\\email.net",
				"mexican\\mexican.net",
				"pro-pro\\pro-pro.net",
				"celegans_metabolic\\celegans_metabolic.net",
				"pgp\\pgp.net",
				"power_grid\\power_grid.gml"
		};
		String[] LFKNetworkFilenames = {
				"LFKnetwork\\100.gml",
				"LFKnetwork\\500.gml",
				"LFKnetwork\\1000.gml",
				"LFKnetwork\\5000.gml",
				"LFKnetwork\\10000.gml",
				"LFKnetwork\\50000.gml",
				"LFKnetwork\\100000.gml",
				"LFKnetwork\\500000.gml",
//				"LFKnetwork\\1000000.gml",
		};
		PrintStream ps = null;
		try {
//			ps = new PrintStream(new FileOutputStream("result_standardlpa_truenetwork.txt"));
			ps = new PrintStream(new FileOutputStream("result_lpam_LFRnetwork.txt"));
//			RunMultiFile(basedir,truthNewworkFilenames,ps);
			RunMultiFile(basedir,LFKNetworkFilenames,ps);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(ps!=null)ps.close();
		}
		
	}
	public void TempFunc(){
		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		GraphNMF gnmf=new GraphNMF(graph,2,100,5);
		gnmf.InitParameter();
		gnmf.trainSymmetric(true);
		graph = gnmf.UpdateNodesWeight();
		/*
		 * Print the per node the weight
		 */
		System.out.println("NodeId\tWeightList");
		for(Vertex v:graph.getVertices()){
			System.out.println(v.getId());
			for(double w:v.getWeight()) {
				System.out.print(w+"\t");
			}
			System.out.println();
		}
		System.out.println("Node similarity with Neighborhood");
		for(Vertex v:graph.getVertices()) {
			for(Vertex neigh:graph.getNeighbors(v)) {
				double node_importance = NodeImportance.getSmoothDegreeImportance(graph, v);
//				double node_importance = NodeImportance.getPageRankImportance(sparse_graph, v);
				System.out.println(v.getId()+"\t"+neigh.getId()+"\t"+Math.sqrt(GraphUtils.calcalueEducianSimilarity(v, neigh)*node_importance));
//				System.out.println(v.getId()+"\t"+neigh.getId()+"\t"+Math.sqrt(GraphUtils.calcalueCosineSimilarity(v, neigh)*neigh_importance));
//				System.out.println(v.getId()+"\t"+neigh.getId()+"\t"+Math.sqrt(GraphUtils.calcaluePearsonSimilarity(v, neigh)*neigh_importance));
			}
		}
		/*
		 * Run the Community Detection Algorithm
		 */
//		NMWeightLPA lpa=new NMWeightLPA(sparse_graph,100);
		StandardLPA lpa=new StandardLPA(graph,1000);
		lpa.run();
		GraphUtils.PrintCommunityCollections(GraphUtils.exportCommunityCollection(graph),"\t");
		
		/*
		 * Measure the Result
		 */
		
		System.out.println("Modularity Q="+MeasureCollections.calculateQ(graph));
		System.out.println("Truth Modularity Q="+MeasureCollections.calculateQForTruth(graph));
		System.out.println("NMI="+MeasureCollections.calculateNMI(GraphUtils.exportCommunityCollection(graph),
								GraphUtils.exportCommunityGroundTruthCollection(graph),graph.getVertexCount()));
		/*
		 * Export to gml File
		 */
		
//		ExportFile.exportAsGML(sparse_graph, gmlfilename.substring(0,gmlfilename.lastIndexOf("."))+"result.gml");
	
	}
}
