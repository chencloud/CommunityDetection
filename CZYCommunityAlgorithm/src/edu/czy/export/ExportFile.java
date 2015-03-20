package edu.czy.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;


import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * @author ZHANPEISEN
 *
 */
public class ExportFile {
	
	public static <V extends Vertex, E extends Edge> void exportAsGML(SparseGraph<V, E> graph, String destPath){
		Iterator<V> vter = graph.getVertices().iterator();
		Iterator<E> eter = graph.getEdges().iterator();
		boolean directed;
		if(graph.getEdgeCount(EdgeType.DIRECTED) != 0)
			directed = true;
		else
			directed = false;
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(destPath));
//			bw.write("Creator \"Zhan Peisen on Mon Jan 14 2012\"" + "\r\n");
			bw.write("graph" + "\r\n");
			bw.write("[" + "\r\n");
			if(directed)
				bw.write("  directed 1" + "\r\n");
			else
				bw.write("  directed 0" + "\r\n");
			while(vter.hasNext()){
				V ver = vter.next();
				bw.write("  node" + "\r\n");
				bw.write("  [" + "\r\n");
				bw.write("    id " + ver.getId() + "\r\n");
				if(ver.getLabel() != null && !ver.getLabel().equals(""))
					bw.write("    label " + ver.getLabel() + "\r\n");
				else
					bw.write("    label " + ver.getId() + "\r\n");
				if(ver.getValue() != null)
					bw.write("    value " + ver.getValue() + "\r\n");
				if(ver.getGroundTruth() != null && !ver.getGroundTruth().equals(""))
					bw.write("    truth " + ver.getGroundTruth() + "\r\n");
				bw.write("  ]" + "\r\n");
			}
			while(eter.hasNext()){
				E edge = eter.next();
				bw.write("  edge" + "\r\n");
				bw.write("  [" + "\r\n");
				if(edge.getLabel() != null)
					bw.write("    label " + edge.getLabel() + "\r\n");
				bw.write("    source " + edge.getSourceID() + "\r\n");
				bw.write("    target " + edge.getTargetID() + "\r\n");
				bw.write("    value " + edge.getWeight() + "\r\n");
				bw.write("  ]" + "\r\n");
			}
			bw.write("]" + "\r\n");
			bw.close();
			System.out.println("Export finished!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static <V extends Vertex, E extends Edge> void exportAsEdgeFile(SparseGraph<V, E> graph, String destPath){
		Iterator<E> eter = graph.getEdges().iterator();
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(destPath));
			while(eter.hasNext()){
				E edge = eter.next();
				bw.write(edge.getSourceID() + "\t" + edge.getTargetID() + "\t" + edge.getWeight() + "\n");
			}
			bw.close();
			System.out.println("Export finished!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

}