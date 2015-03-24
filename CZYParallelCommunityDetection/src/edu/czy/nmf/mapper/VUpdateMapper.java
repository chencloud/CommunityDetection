package edu.czy.nmf.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class VUpdateMapper  extends Mapper<Object,Text,VIntWritable,Text>{
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
		 //int id = Integer.parseInt(strs[0]);
		 String[] Kstrs = strs[2].split(",");
		 for(int i=1;i<=Kstrs.length;i++)
			 context.write(new VIntWritable(i), new Text(strs[0]+"\t"+Kstrs[i]));
	 }
	@Override
	protected void cleanup(Context context
            ) throws IOException, InterruptedException {
		// NOTHING
	}
}
