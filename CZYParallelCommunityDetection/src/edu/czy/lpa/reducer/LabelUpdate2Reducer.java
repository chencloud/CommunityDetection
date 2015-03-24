package edu.czy.lpa.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class LabelUpdate2Reducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{
	private double p;
	private double lamda;
	private int isNormal;
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 p = context.getConfiguration().getDouble("pValue", 0.0);
		 lamda = context.getConfiguration().getDouble("Lamda", 0.5);
		 isNormal = context.getConfiguration().getInt("isNormal", 1);
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		Map<Integer,Double> comMap = new HashMap<Integer,Double>();
		String uRow = null;
		List<NeighNode> neighList = new ArrayList<NeighNode>();
		for(Text v : values) {
			String[] strs = v.toString().split("\\s");
			if("t".equals(strs[1])) {
				if(strs.length == 6) {
					NeighNode  node = new NeighNode();
					node.id = Integer.parseInt(strs[2]);
					node.com = strs[3];
					node.degree = Integer.parseInt(strs[4]);
					node.nRow = strs[5];
					neighList.add(node);
				} else {
					System.err.println("LabelUpdate2Reducers t-type record occur error");
					System.exit(-1);
				}
			} else if("u".equals(strs[1])) {
				uRow = strs[2];
			} else {
				System.err.println("LabelUpdate2Reducer has unknown label (t or u)");
				System.exit(-1);
			}
		}
		//收集标签信息
		for(NeighNode node : neighList){
			double node_diff = calculateDifference(uRow,node.nRow);
			double node_importance = node.degree*1.0/(1+node.degree);
			double comValue = lamda*node_diff+(1-lamda)*node_importance;
			String[] coms = node.com.split(",");
			for(int i=1;i<coms.length;i+=2) {
				Integer comid = Integer.parseInt(coms[i-1]);
				double comBlongness = Double.parseDouble(coms[i]) + comValue;
				if(comMap.containsKey(comid)) {
					comBlongness += comMap.get(comid);
				}
				comMap.put(comid, comBlongness);
			}
		}
		//Find the most popular vote
		Iterator<Entry<Integer, Double>> it = comMap.entrySet().iterator();
		double popularCommunityValue = Double.MIN_VALUE;
		double unpopularCommunityValue =  Double.MAX_VALUE;
		while ( it.hasNext() ) {
			Entry<Integer, Double> entry = it.next();
			if ( entry.getValue() > popularCommunityValue ) {
				popularCommunityValue = entry.getValue();
			}
			if(entry.getValue() < unpopularCommunityValue) {
				unpopularCommunityValue = entry.getValue();
			}
		}
		double throld_c_max = (popularCommunityValue-unpopularCommunityValue)*this.p;
		Collection<Entry<Integer, Double>> cols = new ArrayList<Entry<Integer, Double>>();
		cols.addAll(comMap.entrySet());
//		System.out.println("Before\t"+listener.getId()+"\t"+incomingVotes.size());
		for ( Entry<Integer, Double> col:cols ) {
			if ( (popularCommunityValue-col.getValue()) > throld_c_max ) {
				comMap.remove(col.getKey());
			}
		}
//		System.out.println(listener.getId()+"\t"+incomingVotes.size());
		//Update community distribution of the current node by 1
		Iterator<Entry<Integer, Double>> filteredit = comMap.entrySet().iterator();
		double sumValue = 0.0;
		if(this.isNormal == 1) {
			while ( filteredit.hasNext() ) {
				Entry<Integer, Double> entry = filteredit.next();
				sumValue += entry.getValue();
			}
		}
		filteredit = comMap.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("c").append("\t");
		while ( filteredit.hasNext() ) {
			Entry<Integer, Double> entry = filteredit.next();
			if(this.isNormal == 1)
				sb.append(entry.getKey()).append(",").append(entry.getValue()/sumValue).append(",");
			else 
				sb.append(entry.getKey()).append(",").append(entry.getValue()).append(",");
		}
		context.write(key, new Text(sb.toString()));
	}
	private double calculateDifference(String uRow, String nRow) {
		// TODO Auto-generated method stub
		String[] strKey = uRow.split(",");
		String[] strValue = nRow.split(",");
		double sum = 0.0;
		for(int i=0; i<strKey.length; i++) {
			sum += Math.pow(Double.parseDouble(strKey[i])-Double.parseDouble(strValue[i]),2.0);
		}
		return 1.0/(1.0+sum);
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
	class NeighNode {
		Integer id;
		Integer degree;
		String com ;
		String nRow;
		public NeighNode(){
			
		}
		public NeighNode(Integer id, Integer d , String com , String nRow) {
			this.id = id;
			this.degree = d;
			this.com = com ;
			this.nRow = nRow;
		}
	}
}

