package edu.czy.lpa.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class LabelUpdate1Reducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		String degree = null;
		String com = null;
		String uRow = null;
		List<Integer> neighList = new ArrayList<Integer>();
		for(Text value : values) {
			String[] strs = value.toString().split("\t");
			if("u".equals(strs[0])) {
				uRow = strs[1];
			} else if("d".equals(strs[0])) {
				degree = strs[1];
			} else if ("c".equals(strs[0])) {
				com = strs[1];
			} else if("e".equals(strs[0])) {
				neighList.add(Integer.parseInt(strs[1]));
			} else {
				System.err.println("LabelUpdate1 occur non-known label");
				System.exit(-1);
			}
		}
		if(null == degree || null == com || null == uRow || neighList.isEmpty()) {
			System.err.println("LabelUpdate1 occur null degree or com or uRow or neighList");
			System.exit(-1);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("t").append("\t");
		sb.append(key.toString()).append("\t");
		sb.append(com).append("\t");
		sb.append(degree).append("\t");
		sb.append(uRow);
		for(Integer neighId : neighList) {
			context.write(new VIntWritable(neighId), new Text(sb.toString()));
		}
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}

