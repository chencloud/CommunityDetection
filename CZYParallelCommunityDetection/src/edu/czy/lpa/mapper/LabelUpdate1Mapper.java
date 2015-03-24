package edu.czy.lpa.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class LabelUpdate1Mapper extends Mapper<Object,Text,VIntWritable,Text>{
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
	    String label = strs[1];
	    int id = Integer.parseInt(strs[0]);
	    if("u".equals(label)) {
	    	context.write(new VIntWritable(id), new Text("u\t"+strs[2]));
	    } else if("d".equals(label)) {
	    	context.write(new VIntWritable(id), new Text("d\t"+strs[2]));
	    } else if("c".equals(label)) {
	    	context.write(new VIntWritable(id), new Text("c\t"+strs[2]));
	    } else {//±ß±í
	    	context.write(new VIntWritable(id), new Text("e\t"+strs[1]));
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
