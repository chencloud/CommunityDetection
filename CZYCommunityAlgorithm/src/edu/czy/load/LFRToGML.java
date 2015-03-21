package edu.czy.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.utils.GraphUtils;


public class LFRToGML {

	/**
	 * @param args
	 */
	private String readFile;
	private String readFile2;
	private String writeFile;
	private Map<String, String[]> valueMap;

	public LFRToGML(String readFile, String readFile2, String writeFile) {
		super();
		this.readFile = readFile;
		this.readFile2 = readFile2;
		this.writeFile = writeFile;
	}

	public void readValue() {
		try {
			valueMap = new TreeMap<String, String[]>();
			BufferedReader br = new BufferedReader(new FileReader(new File(
					this.readFile2)));
			String line;
			String node;
			String[] value;
			while ((line = br.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				node = line.substring(0, line.indexOf("\t"));
				value = line.substring(line.indexOf("\t") + 1).split("\\s");
				valueMap.put(node, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void Run() {
		try {
			readValue();
			BufferedReader br = new BufferedReader(new FileReader(new File(
					this.readFile)));
			PrintWriter pw = new PrintWriter(this.writeFile);
			Map<String, Vertex> nodeMap = new LinkedHashMap<String, Vertex>();
			List<Edge> Edges = new ArrayList<Edge>();
			Long sourceid = 0L, targetid = 0L;
			String line = "";
			String source = "";
			String target = "";
			/* read file from disk to memory */
			while ((line = br.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				source = new String(line.split("\\t")[0]);
				target = new String(line.split("\\t")[1]);
				/* add the nodes */
				if (!nodeMap.containsKey(source)) {
					nodeMap.put(source, new Vertex(Long.parseLong(source),source, null,valueMap.get(source)));
				}
				if (!nodeMap.containsKey(target)) {
					nodeMap.put(target, new Vertex(Long.parseLong(target),target, null,valueMap.get(target)));
				}
				sourceid = nodeMap.get(source).getId();
				targetid = nodeMap.get(target).getId();
				/* add the edge */
				Edges.add(new Edge(sourceid, targetid, 1.0));
			}
			br.close();
			/* write memory to disk */

			pw.println("Creator \"Famiking on " + new Date() + "\"" + "\n"
					+ "graph[\n  directed  0");
			List<Vertex> nodeList = new ArrayList<Vertex>(nodeMap.values());
			Collections.sort(nodeList, new Comparator<Object>() {

				@Override
				public int compare(Object arg0, Object arg1) {

					return (int)(((Vertex) arg0).getId() - ((Vertex) arg1).getId());
				}

			});
			for (Vertex node : nodeList) {
				pw.print("  node\n" + " [\n    id " + node.getId()
						+ "\n    label " + node.getLabel()
						+ "\n    value ");
				String[] value = node.getArrayValue();
				for (int i = 0; i < value.length - 1; i++) {
					pw.print(value[i] + GraphUtils.OverlapNode_Split);
				}
				pw.println(value[value.length - 1]);
				pw.println("]");
			}
			for (Edge e : Edges) {
				pw.println("  edge\n  [\n    source " + e.getSourceID()
						+ "\n    target " + e.getTargetID() + "\n  ]");
			}
			pw.println("]");
			pw.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("wrong");
		}
	}

	public static void main(String[] args) {
//		String readfile = "C:\\Users\\famiking\\Desktop\\LFRbenchmark\\LFRbenchmark\\LFR_2\\network.dat";
//		String readfile2 = "C:\\Users\\famiking\\Desktop\\LFRbenchmark\\LFRbenchmark\\LFR_2\\community.dat";
//		String writefile = "C:\\Users\\famiking\\Desktop\\LFRbenchmark\\LFRbenchmark\\LFR_2\\network.gml";
//		LFRToGML tg = new LFRToGML(readfile, readfile2, writefile);
//		tg.Run();

	}

}
