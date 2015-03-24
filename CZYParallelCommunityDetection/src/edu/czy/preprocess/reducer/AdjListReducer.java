package edu.czy.preprocess.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;


public class AdjListReducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{
	private MultipleOutputs<VIntWritable,Text>  mos;//输出到多个不同的文件中
	private String basedir = "";
	private int NodeCount ;
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		mos	= new MultipleOutputs<VIntWritable,Text>(context);//输出到两个不同的文件中
		basedir = context.getConfiguration().get("basedir");
		NodeCount = context.getConfiguration().getInt("NodeCount", 0);
	 }
	@Override
	public void reduce(VIntWritable key, Iterable<Text> values, 
            Context context
            ) throws IOException, InterruptedException {
		int degree = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("a");sb.append("\t");
		Map<Integer,String> vmap = new TreeMap<Integer,String>();
		for(Text value :values) {
			
			String[] strs = value.toString().split("\\s");
			int id = Integer.parseInt(strs[0]);
			vmap.put(id, strs[1]);
			degree +=1;
		}
		for(Integer i=1; i<=NodeCount; i++) {
			if(vmap.containsKey(i)) {
				sb.append(vmap.get(i)).append(",");
			}
			else {
				sb.append(0).append(",");
			}
		}
		context.write(key, new Text(sb.toString()));
		mos.write("Degree", key, new Text("d\t"+String.valueOf(degree)),basedir+"adj/degree");
		mos.write("LocalFind", key, new Text(key.toString()+"\t"+String.valueOf(degree)),basedir+"adj/localfind");
		for(Integer nid:vmap.keySet()) {
			mos.write("LocalFind", new VIntWritable(nid), new Text(key.toString()+"\t"+String.valueOf(degree)),basedir+"adj/localfind");
		}
	}
	@Override
	protected void cleanup(Context context
              ) throws IOException, InterruptedException {
		// NOTHING
	}

	

}
