package edu.czy.nmf.reducer;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class UMultiVReducer extends Reducer<VIntWritable,Text,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		String keyRow = "";
		TreeMap<Integer,String> rowElement= new TreeMap<Integer,String>(); 
		for(Text value:values){
			String[] strs = value.toString().split("\\s");
			int id = Integer.parseInt(strs[0]);
			if(key.get() == id) {
				keyRow = strs[1];
			}
			rowElement.put(id, strs[1]);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("uv").append("\t");
		for(Entry<Integer,String> element : rowElement.entrySet()) {
			sb.append(calcualteVector(keyRow,element.getValue())).append(",");
		}
		context.write(key,new Text( sb.toString()));
	}
	private Double calcualteVector(String keyRow, String value) {
		// TODO Auto-generated method stub
		String[] strKey = keyRow.split(",");
		String[] strValue = value.split(",");
		double sum = 0.0;
		for(int i=0; i<strKey.length; i++) {
			sum += Double.parseDouble(strKey[i])*Double.parseDouble(strValue[i]);
		}
		return sum;
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}
