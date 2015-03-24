package edu.czy.nmf.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class UMultiVMapper extends Mapper<Object,Text,VIntWritable,Text>{
	private int NodeCount ;
	 @Override
	protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 NodeCount = context.getConfiguration().getInt("NodeCount", 0);
	 } 
	 @Override
	public void map(Object key, Text value, Context context
            ) throws IOException, InterruptedException {
		 String[] strs = value.toString().split("\\s");
		 if(strs.length != 3){
			 System.out.println("U not match Error!");
			 System.exit(-1);
		 }
		 int id = Integer.parseInt(strs[0]);
		 for(int i=1;i<=NodeCount;i++){
			 context.write(new VIntWritable(i), new Text(String.valueOf(id)+"\t"+strs[2]));
		 }
	 }
	@Override
	protected void cleanup(Context context
             ) throws IOException, InterruptedException {
		// NOTHING
	}

}
