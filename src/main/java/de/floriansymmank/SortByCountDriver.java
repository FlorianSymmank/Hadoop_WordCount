package de.floriansymmank;

import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SortByCountDriver {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: SortByCountDriver <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Sort By Count");

        job.setJarByClass(SortByCountDriver.class);
        job.setMapperClass(SortByCountMapper.class);
        job.setReducerClass(SortByCountReducer.class);

        job.setMapOutputKeyClass(CompositeKey.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setOutputKeyClass(CompositeKey.class);
        job.setOutputValueClass(NullWritable.class);

        // Set the number of reducers (optional)
        job.setNumReduceTasks(1); // Ensure global ordering

        // Set input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        long startTime = System.currentTimeMillis();

        FileSystem fs = FileSystem.get(conf);

        Path outPath = new Path(args[1]);
        if (fs.exists(outPath)) {
            fs.delete(outPath, true);
        }

        // Wait for completion
        boolean success = job.waitForCompletion(true);

        if (!success) {
            System.err.println("Job failed!");
            System.exit(-1);
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        long totalKeys = job.getCounters().findCounter("KeyCount", "TotalKeys").getValue();
        double keysPerMinute = (totalKeys / (elapsedTime / 60000.0));

        Path path = new Path(args[0]);
        FileStatus fileStatus = fs.getFileStatus(path);

        long inputFileSize = fileStatus.getLen();
        String inputFileName = fileStatus.getPath().getName();

        
        String outputFileName = fs.getFileStatus(outPath).getPath().getName();

        System.out.println("Stats:");
        System.out.println("Output File: " + outputFileName);
        System.out.println("Input File: " + inputFileName);
        System.out.println("Input File Size (bytes): " + inputFileSize);
        System.out.println("Total Keys: " + totalKeys);
        System.out.println("Elapsed Time (ms): " + elapsedTime);

        DecimalFormat df = new DecimalFormat("#");
        System.out.println("Keys per Minute: " + df.format(keysPerMinute));
        System.exit(0);
    }
}
