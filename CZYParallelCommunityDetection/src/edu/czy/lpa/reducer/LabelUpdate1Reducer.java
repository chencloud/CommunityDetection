package edu.czy.lpa.reducer;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class LabelUpdate1Reducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}

