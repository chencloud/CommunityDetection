package edu.czy.preprocess.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;


public class AdjListReducer extends Reducer<VIntWritable,VIntWritable,VIntWritable,Text>{
	private MultipleOutputs<VIntWritable,Text>  mos;//输出到多个不同的文件中
	private String basedir = "";
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		mos	= new MultipleOutputs<VIntWritable,Text>(context);//输出到两个不同的文件中
		basedir = context.getConfiguration().get("basedir");
	 }
	public void reduce(VIntWritable key, Iterable<VIntWritable> values, 
            Context context
            ) throws IOException, InterruptedException {
		int degree = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("a");sb.append("\t");
		List<Integer> vlist = new ArrayList<Integer>();
		for(VIntWritable value :values) {
			vlist.add(value.get());
			sb.append(value.toString()).append(",");
			degree +=1;
		}
		context.write(key, new Text(sb.toString()));
		mos.write("Degree", key, new Text("d\t"+String.valueOf(degree)),basedir+"adj/degree");
		mos.write("LocalFind", key, new Text(key.toString()+"\t"+String.valueOf(degree)),basedir+"adj/localfind");
		for(Integer nid:vlist) {
			mos.write("LocalFind", new VIntWritable(nid), new Text(key.toString()+"\t"+String.valueOf(degree)),basedir+"adj/localfind");
		}
	}
	@Override
	protected void cleanup(Context context
              ) throws IOException, InterruptedException {
		// NOTHING
	}

	

}
