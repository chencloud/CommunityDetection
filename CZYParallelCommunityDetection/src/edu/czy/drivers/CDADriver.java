package edu.czy.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.VIntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import edu.czy.lpa.mapper.LabelInitMapper;
import edu.czy.lpa.mapper.LabelUpdate1Mapper;
import edu.czy.lpa.mapper.LabelUpdate2Mapper;
import edu.czy.lpa.mapper.StopcheckMapper;
import edu.czy.lpa.reducer.LabelInitReducer;
import edu.czy.lpa.reducer.LabelUpdate1Reducer;
import edu.czy.lpa.reducer.LabelUpdate2Reducer;
import edu.czy.lpa.reducer.StopcheckReducer;
import edu.czy.nmf.mapper.UMultiVMapper;
import edu.czy.nmf.mapper.UUpdateMapper;
import edu.czy.nmf.mapper.VUpdateMapper;
import edu.czy.nmf.reducer.UMultiVReducer;
import edu.czy.nmf.reducer.UUpdateReducer;
import edu.czy.nmf.reducer.VUpdateReducer;
import edu.czy.postprocess.mapper.PostProcessAdjListMapper;
import edu.czy.postprocess.mapper.PostProcessFinalMapper;
import edu.czy.postprocess.mapper.PostProcessFirstMapper;
import edu.czy.postprocess.reducer.PostProcessAdjListReducer;
import edu.czy.postprocess.reducer.PostProcessFinalReducer;
import edu.czy.postprocess.reducer.PostProcessFirstReducer;
import edu.czy.preprocess.mapper.AdjListMapper;
import edu.czy.preprocess.mapper.LocalVertexMapper;
import edu.czy.preprocess.mapper.UVInitMapper;
import edu.czy.preprocess.reducer.AdjListReducer;
import edu.czy.preprocess.reducer.LocalVertexReducer;
import edu.czy.preprocess.reducer.UVInitReducer;

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
	
	public static void main(String[] args)throws Exception{
		//parse the parameter list
		//1 iteration for nmf
		//2 probablity for (Cmax-Cj)<=(Cmax-Cmin)*p;
		//3 determine normal for U or not
		if(args.length != 5) {
			System.err.println("Usage: <edgefilePath> <iteration> <p_value(0,1)> <isnormal(0 or 1)> <nodeCount> <iterationLPA>");
		}
		String edgeFilePath = args[0];
		int iteration = Integer.valueOf(args[1]);
		double pValue = Double.valueOf(args[2]);
		int isNormal = Integer.valueOf(args[3]);
		int nodeCount = Integer.valueOf(args[4]);
		int iterationLPA = Integer.valueOf(args[5]);
		Configuration conf = new Configuration();
		String basedir = "/NMFLPA/";
		
		conf.setDouble("pValue", pValue);
		conf.setInt("isNormal", isNormal);
		conf.setStrings("basedir", basedir);
		conf.setInt("NodeCount",nodeCount);
		// 显示实例化分布式文件系统
		FileSystem hdfs_fs = DistributedFileSystem.get(conf);
		//get the adjList
		Job job = new Job(conf, "PreProcessAdjList");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(AdjListMapper.class);
		job.setReducerClass(AdjListReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(edgeFilePath));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"adj/adjList"));
		MultipleOutputs.addNamedOutput(job, "Degree",
				TextOutputFormat.class, IntWritable.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "LocalFind",
				TextOutputFormat.class, IntWritable.class, Text.class);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		//get the k 
		job = new Job(conf, "PreProcessLocalFind");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(LocalVertexMapper.class);
		job.setReducerClass(LocalVertexReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(basedir+"adj/localfind"));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"adj/kfind"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		//can delete adj/localfind
//		/*
//		 * OutFile exist or not
//		 */
//		if (hdfs_fs.exists(new Path(basedir+"adj/kfind"))) {
//			hdfs_fs.delete(new Path(basedir+"adj/kfind"), true);
//		}
		//get UVInit
		job = new Job(conf, "PreProcessUVInit");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(UVInitMapper.class);
		job.setReducerClass(UVInitReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(basedir+"adj/degree"));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"UV/UMatrix_0"));
		MultipleOutputs.addNamedOutput(job, "VMatrix",
				TextOutputFormat.class, IntWritable.class, Text.class);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		//nmf process
		int i=1;
		for(; i<=iteration; i++ ) {
			conf.setInt("CurIteration", i);
			//calculateU*V
			job = new Job(conf, "NMF-UMultiV");
			job.setJarByClass(CDADriver.class);
			job.setMapperClass(UMultiVMapper.class);
			job.setReducerClass(UMultiVReducer.class);
			job.setMapOutputKeyClass(VIntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(VIntWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/UMatrix_"+String.valueOf(i-1)));
			FileOutputFormat.setOutputPath(job, new Path(basedir+"UV/UVMatrix_"+String.valueOf(i)));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
			//calculate A*U U*UT*U U
			job = new Job(conf, "NMF-UpdateU");
			job.setJarByClass(CDADriver.class);
			job.setMapperClass(UUpdateMapper.class);
			job.setReducerClass(UUpdateReducer.class);
			job.setMapOutputKeyClass(VIntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(VIntWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/UMatrix_"+String.valueOf(i-1)));
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/UVMatrix_"+String.valueOf(i)));
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/VMatrix_"+String.valueOf(i-1)));
			FileInputFormat.addInputPath(job, new Path(basedir+"adj/adjList"));
			FileOutputFormat.setOutputPath(job, new Path(basedir+"UV/UMatrix_"+String.valueOf(i)));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
			//merge ,Update V
			job = new Job(conf, "NMF-UpdateV");
			job.setJarByClass(CDADriver.class);
			job.setMapperClass(VUpdateMapper.class);
			job.setReducerClass(VUpdateReducer.class);
			job.setMapOutputKeyClass(VIntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(VIntWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/UMatrix_"+String.valueOf(i)));
			FileOutputFormat.setOutputPath(job, new Path(basedir+"UV/VMatrix_"+String.valueOf(i)));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
			//delte older file i-1, delte UVMatrix_i;UMatrix_(i-1);VMatrix_(i-1);
//			hdfs_fs.delete(new Path(basedir+"UV/UMatrix_"+String.valueOf(i-1)), true);
//			hdfs_fs.delete(new Path(basedir+"UV/UVMatrix_"+String.valueOf(i)), true);
//			hdfs_fs.delete(new Path(basedir+"UV/VMatrix_"+String.valueOf(i-1)), true);
			
		}
		//lpa algorithm
		//初始化
		job = new Job(conf, "LabelInit");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(LabelInitMapper.class);
		//job.setReducerClass(LabelInitReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
//		job.setOutputKeyClass(VIntWritable.class);
//		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(basedir+"adj/degree"));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"com/nodecom_0"));
		conf.setInt("ComNum", nodeCount);
		int curIteration = 1;
		while(curIteration <= iterationLPA) {
			int comNumber = conf.getInt("ComNum", nodeCount);
			//update 标签
			//update 1
			job = new Job(conf, "LabelUpdated1");
			job.setJarByClass(CDADriver.class);
			job.setMapperClass(LabelUpdate1Mapper.class);
			job.setReducerClass(LabelUpdate1Reducer.class);
			job.setMapOutputKeyClass(VIntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(VIntWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(edgeFilePath));
			FileInputFormat.addInputPath(job, new Path(basedir+"com/nodecom_"+String.valueOf(curIteration-1)));
			FileInputFormat.addInputPath(job, new Path(basedir+"adj/degree"));
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/UMatrix_"+String.valueOf(i)));
			FileOutputFormat.setOutputPath(job, new Path(basedir+"com/nodetemp_"+String.valueOf(curIteration)));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
			//update 2
			job = new Job(conf, "LabelUpdated2");
			job.setJarByClass(CDADriver.class);
			job.setMapperClass(LabelUpdate2Mapper.class);
			job.setReducerClass(LabelUpdate2Reducer.class);
			job.setMapOutputKeyClass(VIntWritable.class);
			job.setMapOutputValueClass(Text.class);
			job.setOutputKeyClass(VIntWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(basedir+"com/nodetemp_"+String.valueOf(curIteration)));
			FileInputFormat.addInputPath(job, new Path(basedir+"UV/UMatrix_"+String.valueOf(i)));
			FileOutputFormat.setOutputPath(job, new Path(basedir+"com/nodecom_"+String.valueOf(curIteration)));
			System.exit(job.waitForCompletion(true) ? 0 : 1);
			//can delete node_temp_curiteration file  nodecom_(curiteration-1)
			//检查停止条件
			job = new Job(conf, "StopCheck");
			job.setJarByClass(CDADriver.class);
			job.setMapperClass(StopcheckMapper.class);
			job.setReducerClass(StopcheckReducer.class);
			job.setMapOutputKeyClass(VIntWritable.class);
			job.setMapOutputValueClass(VIntWritable.class);
			job.setOutputKeyClass(VIntWritable.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(basedir+"com/nodecom_"+String.valueOf(curIteration)));
//			FileOutputFormat.setOutputPath(job, new Path(basedir+"com/node"+String.valueOf(curIteration)));		
			FileOutputFormat.setOutputPath(job, new Path(basedir+"com/node_"+String.valueOf(curIteration)));		
			System.exit(job.waitForCompletion(true) ? 0 : 1);
			//delete com/node_curiteration
//			hdfs_fs.delete(new Path(basedir+"com/nodecom_"+String.valueOf(curIteration-1)), true);
//			hdfs_fs.delete(new Path(basedir+"com/node_"+String.valueOf(curIteration)), true);
//			hdfs_fs.delete(new Path(basedir+"com/nodetemp_"+String.valueOf(curIteration)), true);
			
			//check stop condition or not
			int curComNumber = conf.getInt("ComNum", nodeCount);
			if(curComNumber == comNumber) {
				break;
			} else {
				curIteration += 1;
			}
		}
		//后处理，合并删除子社区
		//获取社区-节点集合
		job = new Job(conf, "PostProcessGetCommunity");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(PostProcessAdjListMapper.class);
		job.setReducerClass(PostProcessAdjListReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(basedir+"com/nodecom_"+String.valueOf(curIteration)));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"postprocess/comAdjList"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		//获取待删除社区
		job = new Job(conf, "PostProcessDelCommunity");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(PostProcessFirstMapper.class);
		job.setReducerClass(PostProcessFirstReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(basedir+"postprocess/comAdjList"));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"postprocess/comDel"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		//删除被包含的社区，返回最终社区
		job = new Job(conf, "PostProcessDelAndFinalMergeCommunity");
		job.setJarByClass(CDADriver.class);
		job.setMapperClass(PostProcessFinalMapper.class);
		job.setReducerClass(PostProcessFinalReducer.class);
		job.setMapOutputKeyClass(VIntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(VIntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(basedir+"postprocess/comDel"));
		FileOutputFormat.setOutputPath(job, new Path(basedir+"postprocess/comFinal"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
		/*合并相似社区*/
	}
}
