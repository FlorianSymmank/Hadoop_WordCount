package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: WordCounter <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Word Counter");

        job.setJarByClass(de.floriansymmank.Main.class);

        job.setMapperClass(de.floriansymmank.BookMapper.class);

        job.setCombinerClass(de.floriansymmank.BookReducer.class);
        job.setReducerClass(de.floriansymmank.BookReducer.class);

        job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.TextInputFormat.class);

        job.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setOutputValueClass(org.apache.hadoop.io.IntWritable.class);

        FileInputFormat.addInputPath(job, new org.apache.hadoop.fs.Path(args[0]));

        FileSystem fs = FileSystem.get(conf);

        Path outDir = new Path(args[1]);

        if (fs.exists(outDir)) {
            fs.delete(outDir, true);
        }

        job.setOutputFormatClass(org.apache.hadoop.mapreduce.lib.output.TextOutputFormat.class);
        FileOutputFormat.setOutputPath(job, outDir);

        try {
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
