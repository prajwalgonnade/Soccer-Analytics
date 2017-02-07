package soccerAnalytics;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class soccerMain {

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		// Create a job for the MapReduce task
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "soccerMain");

		// Check if the number of arguments is correct
		if (args.length != 3) {
			System.out.println("Wrong number of arguments: " + args.length);
			System.out
					.println("Valid format of arguments is - <Input File Directory> <Output Destination> <Position name>");
			System.out
					.println("Valid positions - Striker/Midfielder/Defender/Goalkeeper");
			System.exit(-1);
		}
		// Set the Mapper class based on the type of player 
		if (args[2].equalsIgnoreCase("Striker")) {
			
			job.setMapperClass(MyMapperStriker.class);
			job.setCombinerClass(MyCombiner.class);
		} else if (args[2].equalsIgnoreCase("Defender")) {
			
			job.setMapperClass(MyMapperDefender.class);
			job.setCombinerClass(MyCombiner.class);
		} else if (args[2].equalsIgnoreCase("Midfielder")) {
			
			job.setMapperClass(MyMapperMidfielder.class);
			job.setCombinerClass(MyCombiner.class);
		} else if (args[2].equalsIgnoreCase("Goalkeeper")) {
			
			job.setMapperClass(MyMapperGoalkeeper.class);
			job.setCombinerClass(MyCombiner.class);
		} else {
			// Print error message for invalid arguments
			System.out.println("Invalid arguments");
			System.out
					.println("Valid format of arguments is - <Input File Directory> <Output Destination> <Position name>");
			System.out
					.println("Valid positions - Striker/Midfielder/Defender/Goalkeeper");
			System.exit(0);
		}
		// Set the Reducer class
		job.setReducerClass(MyReducer.class);

		// Set the key and value class
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// Set the input and output format class
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		// Set the input and output path
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path("temp"));

		// Create a job for the MapReduce task
		Configuration conf2 = new Configuration();
		Job job2 = Job.getInstance(conf2, "soccerMain2");

		// Set the Mapper and Reducer class
		job2.setMapperClass(MyMapper2.class);
		job2.setReducerClass(MyReducer2.class);

		// Set the key and value class
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);

		// Set the input and output format class
		job2.setInputFormatClass(TextInputFormat.class);
		job2.setOutputFormatClass(TextOutputFormat.class);

		// Set the input and output path
		TextInputFormat.addInputPath(job2, new Path("temp"));
		TextOutputFormat.setOutputPath(job2, new Path(args[1]));

		// Wait for the job to finish
		boolean success = job.waitForCompletion(true);

		boolean successFinal = false;
		Configuration config = new Configuration();
		FileSystem fs = FileSystem.get(config);

		// Delete temporary files after successful execution
		if (success) {
			fs.delete(new Path("temp/_SUCCESS"), false);
			fs.delete(new Path("temp/_logs"), true);

			// Wait for the job to finish
			successFinal = job2.waitForCompletion(true);
		}
		if (successFinal) {
			fs.delete(new Path("temp"), true);
			fs.delete(new Path("user"), true);
		}

	}

}
