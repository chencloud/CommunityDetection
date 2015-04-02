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
import edu.czy.lpa.BLPA;
import edu.czy.lpa.DIFFWLPA;
import edu.czy.lpa.DWLPA;
import edu.czy.lpa.LPA;
import edu.czy.lpa.LPAM;
import edu.czy.lpa.LPAMM;
import edu.czy.lpa.NMFWLPA;
import edu.czy.lpa.StandardLPA;
import edu.czy.measure.MeasureCollections;
import edu.czy.measure.Measure;
import edu.czy.utils.GraphStatistic;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;
/*
 
 */
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

//		NMFWLPA nmfwlpa = new NMFWLPA(graph,100,true,true);
		DIFFWLPA diffwlpa = new DIFFWLPA(graph,100,true,true);

		for(int i=0; i<repeatNum; i++) {
			//run algorithm;set algorithm

//			nmfwlpa.run();

			diffwlpa.run();
//			LPA dwlpa = new DWLPA(graph,10000,true,false);
//			dwlpa.run();
//			LPA standardlpa = new StandardLPA(graph,100);
//			standardlpa.run();
//			LPA lpam = new LPAM(graph,100);
//			lpam.run();
//			LPA blpa = new BLPA(graph,100);
//			blpa.run();
//			LPA lpamm = new LPAMM(graph,100);
//			lpamm.run();
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
	
	public static void RunMultiFile(String basedir,String[] filename,PrintStream pw, boolean isQ, boolean isNMI) {
		for(int i=0;i<filename.length;i++) {
			pw.println("++++++++++++++++++++++++++++++++++++++++++++++++");
			pw.println("Running "+filename[i]);
			pw.println("++++++++++++++++++++++++++++++++++++++++++++++++");
			SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(basedir+filename[i]);
			pw.println("N="+graph.getVertexCount()+";E="+graph.getEdgeCount());
			//set parameter;
			repeatRun(graph,10,isQ,isNMI,pw);
			pw.println("++++++++++++++++++++++++++++++++++++++++++++++++\n");
		}
	}
	public static void main(String[] args)
	{
		String basedir = "E:\\dataset\\unweight_dataset\\";
		String[] truthNewworkFilenames = {
//				"karate\\karate.gml",
//				"dolphins\\dolphins.gml",
//				"polbooks\\polbooks.gml",
//				"jazz\\jazz.net",
				"mexican\\mexican.net",
//				"adjnoun\\adjnoun.gml",
//				"celegans_metabolic\\celegans_metabolic.net",
//				"football\\football.gml",
//				"email\\email.net",
//				"pro-pro\\pro-pro.net",
//				"power_grid\\power_grid.gml",
//				"pgp\\pgp.net",
//				"Internet\\Internet.gml"
		};
		String[] LFKNetworkFilenames = {
//				"LFKnetwork\\100.gml",
//				"LFKnetwork\\500.gml",
//				"LFKnetwork\\1000.gml",
//				"LFKnetwork\\5000.gml",
				"LFKnetwork\\10000.gml",
				"LFKnetwork\\50000.gml",
				"LFKnetwork\\100000.gml",
				"LFKnetwork\\500000.gml",
//				"LFKnetwork\\1000000.gml"
		};
		PrintStream ps = null;
		try {
//			ps = new PrintStream(new FileOutputStream("result_nmflpa_q_truenetwork_normal.txt"));
			ps = new PrintStream(System.out);
			RunMultiFile(basedir,truthNewworkFilenames,ps,true,false);
			
//			ps = new PrintStream(new FileOutputStream("result_nmflpa_q_nmi_LFRnetwork.txt"));
//			RunMultiFile(basedir,LFKNetworkFilenames,ps,true,true);
			
//			ps = new PrintStream(new FileOutputStream("result_blpa_nmi_LFRnetwork.txt"));
//			RunMultiFile(basedir,LFKNetworkFilenames,ps,false,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(ps!=null)ps.close();
		}
		
	}
}
