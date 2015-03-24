package edu.czy.nmf.reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class VUpdateReducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
        Context context
        ) throws IOException, InterruptedException {
		TreeMap<Integer,Double> v = new TreeMap<Integer,Double>();
		for(Text value : values) {
			String[] strs = value.toString().split("\\s");
			int id = Integer.parseInt(strs[1]);
			double w = Double.parseDouble(strs[2]);
			 if(strs.length != 3) {
				 System.err.println("Reduce UVInit not match");
				 System.exit(-1);
			 }
			 if("v".equals(strs[0].trim())) {
					v.put(id, w);		 
			 	}
			 else {
				 System.err.println("Reduce UVInit not match 'u' or 'v' ");
				 System.exit(-1);
			 }
		}
		if(!v.isEmpty()){
			StringBuilder sb = new StringBuilder();
			sb.append("v").append("\t");
			for(Entry<Integer,Double> entry:v.entrySet()) {
				sb.append(entry.getValue()).append(",");
			}
			context.write(key, new Text(sb.toString()));
		}
	}
	@Override
	protected void cleanup(Context context
          ) throws IOException, InterruptedException {
		// NOTHING
	}
}


