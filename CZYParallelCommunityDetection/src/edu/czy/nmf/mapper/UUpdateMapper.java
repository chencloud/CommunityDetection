package edu.czy.nmf.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class UUpdateMapper extends Mapper<Object,Text,VIntWritable,Text>{
	private int NodeCount ;
	private int k;
	 @Override
	protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 NodeCount = context.getConfiguration().getInt("NodeCount", 0);
		 k = context.getConfiguration().getInt("LocalNodeNum", 0);
	 } 
	 @Override
	public void map(Object key, Text value, Context context
           ) throws IOException, InterruptedException {
		 String[] strs = value.toString().split("\\s");
		 if(strs.length != 3){
			 System.out.println("uv v u adjList not match Error!");
			 System.exit(-1);
		 }
		 int id = Integer.parseInt(strs[0]);
		 if("u".equals(strs[1])) {
			 context.write(new VIntWritable(id), value);
		 } else if("v".equals(strs[1])) {
			 for(int i=1; i<=NodeCount; i++) {
				 context.write(new VIntWritable(i), value);
			 }
		 } else if("uv".equals(strs[1])) {
			 for(int i=1; i<=k; i++) {
				 context.write(new VIntWritable(id),new Text(String.valueOf(i)+"\tuv\t"+strs[2]));
			 }
		 } else if("a".equals(strs[1])) {
			 for(int i=1; i<=k; i++) {
				 context.write(new VIntWritable(id),new Text(String.valueOf(i)+"\ta\t"+strs[2]));
			 }
		 }
	 }
	@Override
	protected void cleanup(Context context
            ) throws IOException, InterruptedException {
		// NOTHING
	}
}
