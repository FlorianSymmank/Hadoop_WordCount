package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountDriver {

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Usage: WordCounter <input path> <output path> <stopwords path>");
            System.exit(-1);
        }
        
        Configuration conf = new Configuration();
        conf.set("stopWordsPath", args[2]);

        Job job = Job.getInstance(conf, "Word Counter");

        job.setJarByClass(de.floriansymmank.WordCountDriver.class);

        // Configure mapper and reducer
        job.setMapperClass(de.floriansymmank.WordCountMapper.class);
        job.setCombinerClass(de.floriansymmank.WordCountReducer.class);
        job.setReducerClass(de.floriansymmank.WordCountReducer.class);

        // Configure input
        job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.TextInputFormat.class);
        FileInputFormat.addInputPath(job, new org.apache.hadoop.fs.Path(args[0]));

        // Configure output
        FileSystem fs = FileSystem.get(conf);

        Path outDir = new Path(args[1]);

        if (fs.exists(outDir)) {
            fs.delete(outDir, true);
        }

        FileOutputFormat.setOutputPath(job, outDir);
        job.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setOutputValueClass(org.apache.hadoop.io.IntWritable.class);
        job.setOutputFormatClass(org.apache.hadoop.mapreduce.lib.output.TextOutputFormat.class);

        // Add the JSON stopwords file to the distributed cache
        job.addCacheFile(new Path(args[2]).toUri());

        try {
            System.exit(job.waitForCompletion(true) ? 0 : 1);
        } catch (InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
