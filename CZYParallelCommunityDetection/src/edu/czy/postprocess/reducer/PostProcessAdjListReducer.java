package edu.czy.postprocess.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class PostProcessAdjListReducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		TreeSet<Integer> nodeList = new TreeSet<Integer>();
		TreeSet<Integer> NeighCList = new TreeSet<Integer>();
		for(Text value : values) {
			String[] strs = value.toString().split("\\s");
			int id = Integer.parseInt(strs[1]);
			if("n".equals(strs[0])) {
				nodeList.add(id);
			} else if("r".equals(strs[0])) {
				NeighCList.add(id);
			} else {
				System.err.println("unknown label,not r or n");
				  System.exit(-1);
			}
		}
		StringBuilder sb = new StringBuilder();
		for(Integer nodeid : nodeList) {
			sb.append(nodeid).append(",");
		}
		for(Integer neighCid : NeighCList) {
			context .write(new VIntWritable(neighCid), new Text(key.toString()+"\t"+sb.toString()));
		}
		context .write(key, new Text(key.toString()+"\t"+sb.toString()));
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}
