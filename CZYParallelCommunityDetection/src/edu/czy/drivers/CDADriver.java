package edu.czy.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

public class CDADriver {

	
	
	public void exampleFunc(){
//		Configuration conf = new Configuration();
//		conf.addResource("core-site.xml");
//		conf.addResource("hdfs-site.xml");
//		String[] otherArgs = new GenericOptionsParser(conf, args)
//				.getRemainingArgs();
//		// long ldifftime=10;
//		String kakouInfoTable = "/CarData/KaKouInfo";
//		if (otherArgs.length != 3) {
//
//			System.err.println("Usage:  <in> <out> <KaKouInfoTableDir>");
//			System.exit(2);
//		}
//		kakouInfoTable = otherArgs[2];
//
//		int highwayMaxSpeed = 150;
//		int roadwayMaxSpeed = 150;
//		int maxdistance = 60;
//		int maxTimeDiff = 5;
//		// String HighwayMaxSpeed=otherArgs[3];
//		// if(!HighwayMaxSpeed.equals(""))
//		// highwayMaxSpeed=Integer.parseInt(HighwayMaxSpeed);
//		// String RoadMaxSpeed=otherArgs[4];
//		// if(!RoadMaxSpeed.equals(""))
//		// roadwayMaxSpeed=Integer.parseInt(RoadMaxSpeed);
//		// String maxDistance=otherArgs[5];
//		// if(!maxDistance.equals(""))
//		// maxdistance=Integer.parseInt(maxDistance);
//		// String maxTimediff=otherArgs[6];
//		// if(!maxTimediff.equals(""))
//		// maxTimeDiff=Integer.parseInt(maxTimediff);
//		/* 保存参数 */
//		conf.setInt("MaxHighWaySpeed", highwayMaxSpeed);
//		conf.setInt("MaxRoadSpeed", roadwayMaxSpeed);
//		conf.setInt("MaxDistance", maxdistance);
//		conf.setInt("MaxTimeDistict", maxTimeDiff);
//		/*
//		 * distribute the cache files of kakou info files
//		 */
//		// 显示实例化分布式文件系统
//		FileSystem hdfs_fs = DistributedFileSystem.get(conf);
//		Path kakouInfoPath = new Path(kakouInfoTable);
//		FileStatus[] fileLists = hdfs_fs.listStatus(kakouInfoPath);
//		for (FileStatus fs : fileLists) {
//			DistributedCache.addCacheFile(fs.getPath().toUri(), conf);
//		}
//
//		/*
//		 * OutFile exist or not
//		 */
//		if (hdfs_fs.exists(new Path(otherArgs[1]))) {
//			hdfs_fs.delete(new Path(otherArgs[1]), true);
//		}
//		/*
//		 * OutFile exist or not
//		 */
//		if (hdfs_fs.exists(new Path("/CarData/out_errorData"))) {
//			hdfs_fs.delete(new Path("/CarData/out_errorData"), true);
//		}
//		hdfs_fs.close();
//		Job job = new Job(conf, "CarRecon");
//
//		job.setJarByClass(CarDriver.class);
//		job.setMapperClass(CarMapper.class);
//		job.setReducerClass(CarReducer.class);
//		job.setMapOutputKeyClass(Text.class);
//		job.setMapOutputValueClass(CheLiuInfoWritable.class);
//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(Text.class);
//		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
//		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
//		MultipleOutputs.addNamedOutput(job, "ErrorData",
//				TextOutputFormat.class, Text.class, Text.class);
//		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	public static void main(String[] args){
		//parse the parameter list
	}
}
