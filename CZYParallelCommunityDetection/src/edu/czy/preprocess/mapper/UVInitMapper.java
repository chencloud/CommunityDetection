package edu.czy.preprocess.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class UVInitMapper extends Mapper<Object, Text, VIntWritable, Text>{
	private int k = 2;//初始化最小值
	private int nodeCount;
	 @Override
	protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 k = context.getConfiguration().getInt("LocalNodeNum", 2);
		 nodeCount = context.getConfiguration().getInt("NodeCount",0);
	 } 
	 @Override
	public void map(Object key, Text value, Context context
            ) throws IOException, InterruptedException {
		 String[] strs = value.toString().split("\\s");
		 if(strs.length != 3) {
			 System.err.println("DegreeFile not match");
			 System.exit(-1);
		 }
		 int nodeid = Integer.parseInt(strs[0]);
		 for(int i=1;i<=k;i++) {
			 double w = Math.random();
			 context.write(new VIntWritable(nodeid), new Text("u\t"+String.valueOf(k)+"\t"+String.valueOf(w)));
			 context.write(new VIntWritable(k), new Text("v\t"+String.valueOf(nodeid)+"\t"+String.valueOf(w)));
		 }
	 }
	@Override
	protected void cleanup(Context context
             ) throws IOException, InterruptedException {
		// NOTHING
	}
}

