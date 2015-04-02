package edu.czy.overlap.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Copra.COPRA;
import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.export.ExportFile;
import edu.czy.load.LoadGroundTruthFile;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * 采用同步更新策略
 * Not Implemented
 */
public class Copra {
	public static void main(String[] args){
    	COPRA copra = new COPRA();
    	String tempfileName = "copra_temp.edge";
    	List<String> argsList = new ArrayList<String>();
    	for(int i=0;i<args.length;i++){
    		argsList.add(args[i]);
    	}
//		String filename="E:\\dataset\\unweight_dataset\\email\\email.net";
		String filename="E:\\dataset\\unweight_dataset\\karate\\karate.gml";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		ExportFile.exportAsEdgeFile(graph, "copra_temp.edge");
		argsList.add("copra_temp.edge");
		argsList.add("-v");
		argsList.add("1");
//		argsList.add("10");
		argsList.add("-mo");
//		argsList.add("-extrasimplify");
//		argsList.add("-stats");
//		argsList.add("1");
//		argsList.add("-repeat");
//		argsList.add("20");
		copra.run(argsList.toArray(new String[0]));
		Collection<Collection<Integer>> result = 
				LoadGroundTruthFile.loadGroundTruthCommunityPerLine("clusters-copra_temp.edge","\\s");
    	System.out.println("Eq="+
//		MeasureCollections.calculateQovFromCollectionsWithInteger(graph, result));
    	MeasureCollections.calculateEqWithInteger(graph, result));
    	
    	
    }
}
