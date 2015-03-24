package edu.czy.nmf.reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class UUpdateReducer  extends Reducer<VIntWritable,Text,VIntWritable,Text>{
	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		String Urow = null;
		TreeMap<Integer,Map<String,String>> elements = new TreeMap<Integer,Map<String,String>>();
		for(Text value : values) {
			String[] strs = value.toString().split("\\s");
			Integer k = Integer.parseInt(strs[0]);
			if("u".equals(strs[1]) && k.intValue() == key.get()) {
				Urow = strs[1];
			}
			else {
				if(!elements.containsKey(k)){
					elements.put(k, new HashMap<String,String>());
				}
				elements.get(k).put(strs[1], strs[2]);
			}
		}
		if(Urow == null) {System.out.println("Urow Reducer not Found!");System.exit(-1);}
		String[] urow = Urow.split(",");
		StringBuilder sbU = new StringBuilder();
		sbU.append("u").append("\t");
		for(Entry<Integer,Map<String,String>> entry : elements.entrySet()) {
			int index = entry.getKey();
			Map<String,String> e = entry.getValue();
			if(e.size() != 3) {
				System.out.println("Urow UV or V or adj not Found!");System.exit(-1);
			}
			double au = calcualteVector(e.get("a"),e.get("v"));
			double uvu = calcualteVector(e.get("uv"),e.get("v"));
			double newUK = Double.parseDouble(urow[index])*au/uvu;
			sbU.append(newUK).append(",");
		}
		context.write(key, new Text(sbU.toString()));
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

