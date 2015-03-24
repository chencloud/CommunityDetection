package edu.czy.preprocess.reducer;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class UVInitReducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{
	private MultipleOutputs<VIntWritable,Text>  mos;//输出到多个不同的文件中
	private String basedir = "";
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		mos	= new MultipleOutputs<VIntWritable,Text>(context);//输出到两个不同的文件中
		basedir = context.getConfiguration().get("basedir");
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
           Context context
           ) throws IOException, InterruptedException {
		TreeMap<Integer,Double> u = new TreeMap<Integer,Double>();
		TreeMap<Integer,Double> v = new TreeMap<Integer,Double>();
		for(Text value : values) {
			String[] strs = value.toString().split("\\s");
			int id = Integer.parseInt(strs[1]);
			double w = Double.parseDouble(strs[2]);
			 if(strs.length != 3) {
				 System.err.println("Reduce UVInit not match");
				 System.exit(-1);
			 }
			 if("u".equals(strs[0].trim())) {
				 u.put(id,w );
			 }
			 else if("v".equals(strs[0].trim())) {
					v.put(id, w);		 
			 	}
			 else {
				 System.err.println("Reduce UVInit not match 'u' or 'v' ");
				 System.exit(-1);
			 }
		}
		
		if(!u.isEmpty()){
			StringBuilder sb = new StringBuilder();
			sb.append("u").append("\t");
			for(Entry<Integer,Double> entry:u.entrySet()) {
				sb.append(entry.getValue()).append(",");
			}
			context.write(key, new Text(sb.toString()));
		}
		if(!v.isEmpty()){
			StringBuilder sb = new StringBuilder();
			sb.append("v").append("\t");
			for(Entry<Integer,Double> entry:u.entrySet()) {
				sb.append(entry.getValue()).append(",");
			}
			mos.write("VMatrix", key, new Text(sb.toString()),basedir+"UV/VMatrix_0");
		}
	}
	@Override
	protected void cleanup(Context context
             ) throws IOException, InterruptedException {
		// NOTHING
	}

	

}

