package edu.czy.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.uci.ics.jung.graph.SparseGraph;

public class LactorVectorUtils {

	public void exportLactorVectorFromVertex(Collection<Vertex> vs , String filename) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			for(Vertex v : vs) {
				Long id = v.getId();
				List<Double> wList = v.getWeight();
				bw.write(id.intValue());
				bw.write("\t");
				for( double w : wList ) {
					bw.write(String.valueOf(w)+",");
				}
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void LoadLactorVectorFromVertex(SparseGraph<Vertex,Edge> graph, String filename) {
		BufferedReader br = null;
		try {
			Map<Long,Vertex> nodemap = new HashMap<Long,Vertex>();
			for(Vertex v : graph.getVertices() ) {
				nodemap.put(v.getId(), v);
			}
			br = new BufferedReader(new FileReader(filename));
			String line = "";
			while((line=br.readLine())!=null) {
				String[] strs = line.trim().split("\t");
				Long id = Long.parseLong(strs[0]);
				List<Double> wList = new ArrayList<Double>();
				for(String str : strs[1].split(",")) {
					wList.add(Double.parseDouble(str));
				}
				nodemap.get(id).setWeight(wList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
