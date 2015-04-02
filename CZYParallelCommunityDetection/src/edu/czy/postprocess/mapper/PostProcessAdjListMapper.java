package edu.czy.postprocess.mapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class PostProcessAdjListMapper extends Mapper<Object,Text,VIntWritable,Text>{
	  /**
	   * Called once at the beginning of the task.
	   */
	  protected void setup(Context context
	                       ) throws IOException, InterruptedException {
	    // NOTHING
	  }

	  /**
	   * Called once for each key/value pair in the input split. Most applications
	   * should override this, but the default is the identity function.
	   */
	  @Override
	  protected void map(Object key, Text value, 
	                     Context context) throws IOException, InterruptedException {
		  String[] strs = value.toString().split("\\s");
		  if(!"c".equals(strs[1])) {
			  System.err.println("unknown label,not c");
			  System.exit(-1);
		  }
		  int nodeid = Integer.parseInt(strs[0]);
		  String[] coms = strs[2].split(",");
		  List<Integer> comList = new ArrayList<Integer>();
		  for( int i=0; i<coms.length;i+=2){
			  int comid = Integer.parseInt(coms[i]);
			  context.write(new VIntWritable(comid), new Text("n\t"+strs[0]));
		  }
		  coms = null;
		  Collections.sort(comList);
		  for( int i=0; i<comList.size(); i++) {
			  for(int j=i+1; j<comList.size(); j++) {
				  context.write(new VIntWritable(comList.get(i)), new Text("r\t"+comList.get(i)));
			  }
		  }
	  }

	  /**
	   * Called once at the end of the task.
	   */
	  protected void cleanup(Context context
	                         ) throws IOException, InterruptedException {
	    // NOTHING
	  }
	

}