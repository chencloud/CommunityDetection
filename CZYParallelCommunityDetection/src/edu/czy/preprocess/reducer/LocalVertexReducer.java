package edu.czy.preprocess.reducer;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class LocalVertexReducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{
	private int k = 0; 
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
          Context context
          ) throws IOException, InterruptedException {
		int maxDegree = 0;
		int keyDegree = 0;
		for(Text value : values){
			String[] strs = value.toString().split("\\s");
			int id = Integer.parseInt(strs[1]);
			int degree = Integer.parseInt(strs[2]);
			if(maxDegree < degree) {
				maxDegree = degree;
			}
			if(id==key.get()){
				keyDegree = degree;
			}
		}
		if(maxDegree == keyDegree){
			k +=1;
		}
	}
	@Override
	protected void cleanup(Context context
            ) throws IOException, InterruptedException {
		// NOTHING
		context.getConfiguration().setInt("LocalNodeNum", k);
	}
}
