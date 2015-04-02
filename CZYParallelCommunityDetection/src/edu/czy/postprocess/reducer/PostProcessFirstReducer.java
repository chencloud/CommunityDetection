package edu.czy.postprocess.reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class PostProcessFirstReducer  extends Reducer<VIntWritable,Text,VIntWritable,Text>{

	@Override
	 protected void setup(Context context
	            ) throws IOException, InterruptedException {
		 
	 }
	public void reduce(VIntWritable key, Iterable<Text> values, 
         Context context
         ) throws IOException, InterruptedException {
		Map<Integer,Collection<Integer>> comMap = new HashMap<Integer,Collection<Integer>>();
		String nodeString = "";
		for(Text value : values) {
			String[] strs = value.toString().split("\\s");
			List<Integer> nodeList = new ArrayList<Integer>();
			String[] nodes = strs[1].split(",");
			for(String node : nodes) {
				nodeList.add(Integer.parseInt(node));
			}
			comMap.put(Integer.parseInt(strs[0]), nodeList);
			if(Integer.parseInt(strs[0])== key.get()) {
				nodeString = strs[1];
			}
		}
		Collection<Integer> keyNodes = comMap.get(key.get());
		for(Entry<Integer,Collection<Integer>> entry : comMap.entrySet()) {
			int cid = entry.getKey();
			if(key.get() != cid) {
				Collection<Integer> cnodes = entry.getValue();
				boolean isSame = true;
				if(cnodes.size() >= keyNodes.size()) {
					for(Integer nodeid :cnodes) {
						if(!keyNodes.contains(nodeid)) {
							isSame = false; break;
						}
					}
					if(isSame) {
						context.write(key, new Text("del"));
					}
				} else {
					for(Integer nodeid :keyNodes) {
						if(!cnodes.contains(nodeid)) {
							isSame = false; break;
						}
					}
					if(isSame) {
						context.write(new VIntWritable(cid), new Text("del"));
					}
				}
			}
		}
		context.write(key, new Text(nodeString));
	}
	@Override
	protected void cleanup(Context context
           ) throws IOException, InterruptedException {
		// NOTHING
	}
}
