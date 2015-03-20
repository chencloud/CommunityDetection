package edu.czy.load;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;
import edu.uci.ics.jung.graph.SparseGraph;

public class LoadGroundTruthFile {
	public static Collection<Collection<Integer>> loadGroundTruthCommunity(String filename,String delim) {
		Map<String,Collection<Integer>> results = new HashMap<String,Collection<Integer>>();
		BufferedReader loader = null;
		try {
			loader = new BufferedReader(new FileReader(filename));
			String line = "";
			while((line = loader.readLine())!= null) {
				if(line.split(delim).length==2) {
					String[] strs = line.split(delim);
					Integer id = Integer.parseInt(strs[0]);
					String values = strs[1];
					if(values.split(GraphUtils.OverlapNode_Split).length>1) {
						for(String value:values.split(GraphUtils.OverlapNode_Split)) {
							if(!results.containsKey(values)) {
								results.put(values, new ArrayList<Integer>());
							}
							results.get(values).add(id);
						}
					} else {
						if(!results.containsKey(values)) {
							results.put(values, new ArrayList<Integer>());
						}
						results.get(values).add(id);
					}
				}else {
					throw new Exception("the length isn't equal to 2");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(loader != null){
				try {
					loader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		return results.values();
	}
	public static void loadGroundTruthCommunityToGraph(String filename,SparseGraph<Vertex,Edge> graph,String delim) {
		BufferedReader loader = null;
		try {
			loader = new BufferedReader(new FileReader(filename));
			String line = "";
			while((line = loader.readLine())!= null) {
				if(line.split("\t").length==2) {
					String[] strs = line.split(delim);
					Integer id = Integer.parseInt(strs[0]);
					String values = strs[1];
					boolean isSeted = false;
					for(Vertex v:graph.getVertices()){
						if(v.getId()==id){
							v.setGroundTruth(values);
							isSeted = true;
							break;
						}
					}
					if(!isSeted){
						throw new Exception("id "+id+" node not be found in graph");
					}
				}else {
					throw new Exception("the length isn't equal to 2");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(loader != null){
				try {
					loader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Collection<Collection<Integer>> loadGroundTruthCommunityWithComIdPerLine(String filename,String delim) {
		Map<String,Collection<Integer>> results = new HashMap<String,Collection<Integer>>();
		BufferedReader loader = null;
		try {
			loader = new BufferedReader(new FileReader(filename));
			String line = "";
			while((line = loader.readLine())!= null) {
				if(line.split(delim).length<2) {
					throw new Exception("the length isn't equal to 2");
				}
				String[] strs = line.split(delim);
				String cid = strs[0];
				results.put(cid, new ArrayList<Integer>());
				for(int i=1;i<strs.length;i++) {
					results.get(cid).add(Integer.parseInt(strs[i]));
				}
				

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(loader != null){
				try {
					loader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		return results.values();
	}
	public static Collection<Collection<Integer>> loadGroundTruthCommunityPerLine(String filename,String delim) {
		Map<String,Collection<Integer>> results = new HashMap<String,Collection<Integer>>();
		BufferedReader loader = null;
		try {
			loader = new BufferedReader(new FileReader(filename));
			String line = "";
			int linecount = 0;
			while((line = loader.readLine())!= null) {
				linecount++;
				if(line.split(delim).length<1) {
					throw new Exception("the length isn't equal to 2");
				}
				String[] strs = line.split(delim);
				results.put(String.valueOf(linecount), new ArrayList<Integer>());
				for(int i=0;i<strs.length;i++) {
					results.get(linecount).add(Integer.parseInt(strs[i]));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(loader != null){
				try {
					loader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		return results.values();
	}
}
