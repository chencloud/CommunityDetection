package edu.czy.preprocess.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class LocalVertexMapper extends Mapper<Object, Text, VIntWritable, Text>{
	 @Override
	protected void setup(Context context
	            ) throws IOException, InterruptedException {
	 } 
	 @Override
	public void map(Object key, Text value, Context context
           ) throws IOException, InterruptedException {
		 String[] str=value.toString().split("\\s");
		 if(str.length != 3){
			 System.err.println("localfind file not match");
			 System.exit(-1);
		 }
		 int id = Integer.parseInt(str[0]);
		 context.write(new VIntWritable(id), value);
	 }
	@Override
	protected void cleanup(Context context
            ) throws IOException, InterruptedException {
		// NOTHING
	}
}
