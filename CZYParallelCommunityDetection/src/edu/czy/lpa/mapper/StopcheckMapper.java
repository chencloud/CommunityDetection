package edu.czy.lpa.mapper;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class StopcheckMapper extends Mapper<Object,Text,VIntWritable,VIntWritable>{
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
	    String[] coms = strs[2].split(",");
		for(int i=1;i<coms.length;i+=2) {
			Integer comid = Integer.parseInt(coms[i-1]);
			context.write(new VIntWritable(1), new VIntWritable(comid));
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
