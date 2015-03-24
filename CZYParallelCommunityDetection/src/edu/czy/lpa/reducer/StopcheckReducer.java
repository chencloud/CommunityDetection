package edu.czy.lpa.reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class StopcheckReducer extends Reducer<VIntWritable,VIntWritable,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<VIntWritable> values, 
         Context context
         ) throws IOException, InterruptedException {
		Set<Integer> comSet = new HashSet<Integer>();
		for(VIntWritable comid : values) {
			comSet.add(comid.get());
		}
		context.getConfiguration().setInt("ComNum", comSet.size());
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}


