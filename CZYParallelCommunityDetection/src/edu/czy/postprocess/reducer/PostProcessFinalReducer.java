package edu.czy.postprocess.reducer;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class PostProcessFinalReducer  extends Reducer<VIntWritable,Text,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		String nodeString = "";
		for(Text value : values) {
			String[] strs = value.toString().split("\\s");
			if("del".equals(strs[1])) {
				return;
			} else {
				nodeString = strs[1];
			}
		}
		if("".equals(nodeString)) {
			System.err.println("gather id="+key.get()+" community failed");
			System.exit(-1);
		}
		context.write(key, new Text(nodeString));
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}
