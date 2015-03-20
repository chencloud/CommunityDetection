package edu.czy.main;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.export.ExportFile;
import edu.czy.factorization.GraphNMF;
import edu.czy.importance.NodeImportance;
import edu.czy.load.LoadEdgeFile;
import edu.czy.load.LoadGML;
import edu.czy.load.LoadGroundTruthFile;
import edu.czy.lpa.NMFWLPA;
import edu.czy.lpa.StandardLPA;
import edu.czy.measure.MeasuerCollections;
import edu.czy.measure.Measure;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public class MainDriver {
	public static void main(String[] args)
	{
		/*
		 * load the graph
		 */
//		String edgefilename = "J:\\paperproject\\DataSet\\toy_network.txt";
//		String gmlfilename="J:\\paperproject\\DataSet\\karate\\karate.gml";
//		String gmlfilename="J:\\paperproject\\DataSet\\dolphins\\dolphins.gml";
//		String gmlfilename="J:\\paperproject\\DataSet\\football\\football.gml";
		String gmlfilename="J:\\paperproject\\DataSet\\polbooks\\polbooks.gml";
		LoadGML<Vertex,Edge> loadGML=new LoadGML<Vertex,Edge>(Vertex.class,Edge.class);
		SparseGraph<Vertex,Edge> sparse_graph=loadGML.loadGraph(gmlfilename);
//		LoadEdgeFile<Vertex,Edge> loadEdge = new LoadEdgeFile<Vertex,Edge>(Vertex.class,Edge.class);
//		SparseGraph<Vertex,Edge> sparse_graph = loadEdge.loadGraph(edgefilename);
		System.out.println("The grpah'edge count:"+sparse_graph.getEdgeCount());
		System.out.println("The graph'node count:"+sparse_graph.getVertexCount());
//		/*load groundtruth*/
//		LoadGroundTruthFile.loadGroundTruthCommunityToGraph("J:\\paperproject\\DataSet\\toy_network_with_groundtruth.txt", sparse_graph,"\\s");
		/*
		 * Run the NMF for gaining the vertex's features
		 */
		GraphNMF gnmf=new GraphNMF(sparse_graph,2,100,5);
		gnmf.InitParameter();
		gnmf.trainSymmetric(true);
		sparse_graph = gnmf.UpdateNodesWeight();
		/*
		 * Print the per node the weight
		 */
		System.out.println("NodeId\tWeightList");
		for(Vertex v:sparse_graph.getVertices()){
			System.out.println(v.getId());
			for(double w:v.getWeight()) {
				System.out.print(w+"\t");
			}
			System.out.println();
		}
		System.out.println("Node similarity with Neighborhood");
		for(Vertex v:sparse_graph.getVertices()) {
			for(Vertex neigh:sparse_graph.getNeighbors(v)) {
				double node_importance = NodeImportance.getSmoothDegreeImportance(sparse_graph, v);
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
		StandardLPA lpa=new StandardLPA(sparse_graph,1000);
		lpa.run();
		GraphUtils.PrintCommunityCollections(GraphUtils.exportCommunityCollection(sparse_graph),"\t");
		
		/*
		 * Measure the Result
		 */
		
		System.out.println("Modularity Q="+MeasuerCollections.calculateQ(sparse_graph));
		System.out.println("Truth Modularity Q="+MeasuerCollections.calculateQForTruth(sparse_graph));
		System.out.println("NMI="+MeasuerCollections.calculateNMI(GraphUtils.exportCommunityCollection(sparse_graph),
								GraphUtils.exportCommunityGroundTruthCollection(sparse_graph),sparse_graph.getVertexCount()));
		/*
		 * Export to gml File
		 */
		
//		ExportFile.exportAsGML(sparse_graph, gmlfilename.substring(0,gmlfilename.lastIndexOf("."))+"result.gml");
	}
}
