package de.floriansymmank;

import org.apache.hadoop.conf.Configuration;
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

        // Wait for completion
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}