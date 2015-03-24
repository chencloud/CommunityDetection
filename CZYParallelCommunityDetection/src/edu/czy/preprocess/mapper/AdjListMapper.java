package edu.czy.preprocess.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class AdjListMapper extends Mapper<Object, Text, VIntWritable, Text>{
	 @Override
	protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 } 
	 @Override
	public void map(Object key, Text value, Context context
             ) throws IOException, InterruptedException {
		 String[] strs = value.toString().split("\\s");
		 if(strs.length != 2 && strs.length != 3){
			 System.exit(-1);
		 }
		 double w = strs.length ==3 ?Double.parseDouble(strs[3]):1.0;
		 VIntWritable sourceId = new VIntWritable();
		 sourceId.set(Integer.valueOf(strs[0]));
		 VIntWritable targetId = new VIntWritable();
		 targetId.set(Integer.valueOf(strs[1]));
		 context.write(sourceId, new Text(targetId.toString()+"\t"+String.valueOf(w)));
		 context.write(targetId,new Text(sourceId.toString()+"\t"+String.valueOf(w)));
	 }
	@Override
	protected void cleanup(Context context
              ) throws IOException, InterruptedException {
		// NOTHING
	}
}
