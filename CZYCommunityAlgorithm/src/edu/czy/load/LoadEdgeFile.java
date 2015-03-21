package edu.czy.load;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class LoadEdgeFile<V extends Vertex,E extends Edge> {
	private Class<V> classV;
	private Class<E> classE;
	SparseGraph<V, E> graph;
	Map<Long, V> nodeMap;
	EdgeType edgeType;
	
	public LoadEdgeFile(Class<V> classV, Class<E> classE){
		this.classV = classV;
		this.classE = classE;
		graph = new SparseGraph<V, E>();
		nodeMap = new HashMap<Long, V>();
		edgeType = EdgeType.UNDIRECTED;
	}
	
	public SparseGraph<V, E> loadGraph(String filePath){
		BufferedReader loader = null;
		String line = null;
		String[] item = null;
		try{
			loader = new BufferedReader(new FileReader(filePath));
			while((line = loader.readLine()) != null){
				
				line = line.trim();
				if(line.startsWith("*")||line.startsWith("#")){
					continue;
				}
				StringBuilder sb = new StringBuilder();
				sb.append(line.charAt(0));
				for(int i=1;i<line.length();i++) {
					if(line.charAt(i)!=' ' && line.charAt(i)!='\t'){
						sb.append(line.charAt(i));
					} else if (line.charAt(i-1)!=' ' && line.charAt(i-1)!='\t'){
						sb.append(line.charAt(i));
					}
				}
				line = sb.toString();
//				System.out.println(line);
				item = line.trim().split("\\s");
				if(item.length !=2 && item.length != 3){
					continue;
				}
				long source = Long.parseLong(item[0].trim());
				long target = Long.parseLong(item[1].trim());
				if(source == target)
					continue;
				double weight = 1D;
				if(item.length > 2)
					weight = Double.parseDouble(item[2].trim());
//				String label = "undir";
//				if(item.length == 4)
//					label = item[3].trim();
				V s_ver, t_ver;
				if(!nodeMap.containsKey(source)){
					s_ver = classV.newInstance();
					s_ver.setId(source);
					nodeMap.put(source, s_ver);
					graph.addVertex(s_ver);
				}else
					s_ver = nodeMap.get(source);
				if(!nodeMap.containsKey(target)){
					t_ver = classV.newInstance();
					t_ver.setId(target);
					nodeMap.put(target, t_ver);
					graph.addVertex(t_ver);
				}else
					t_ver = nodeMap.get(target);
				E edge = classE.newInstance();
				edge.setSourceID(source);
				edge.setTargetID(target);
				edge.setWeight(weight);
//				edge.setLabel(label);
				graph.addEdge(edge, s_ver, t_ver, edgeType);
			}
			loader.close();
			System.out.println("There are " + graph.getVertexCount() + " nodes and " + graph.getEdgeCount() + " edges in the graph!");
		}catch(Exception e){
			e.printStackTrace();
		}
		return graph;
	}
	
}
