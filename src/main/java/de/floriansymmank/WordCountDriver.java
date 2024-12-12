package de.floriansymmank;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountDriver {

    public static void main(String[] args) throws IOException, IllegalArgumentException, InterruptedException {

        if (args.length != 4) {
            System.err.println("Usage: WordCounter <lang> <input path> <output path> <stopwords path>");
            System.exit(-1);
        }

        String lang = args[0];
        String inputPath = args[1];
        String outputPath = args[2];
        String stopWordsPath = args[3];

        Configuration conf = new Configuration();
        conf.set("lang", lang);
        conf.set("stopWordsPath", stopWordsPath);

        Job job = Job.getInstance(conf, "Word Counter");

        job.setJarByClass(de.floriansymmank.WordCountDriver.class);

        // Configure mapper and reducer
        job.setMapperClass(de.floriansymmank.WordCountMapper.class);
        job.setCombinerClass(de.floriansymmank.WordCountReducer.class);
        job.setReducerClass(de.floriansymmank.WordCountReducer.class);

        // Configure input
        job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.TextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));

        // Configure output
        FileSystem fs = FileSystem.get(conf);

        Path outDir = new Path(outputPath);

        if (fs.exists(outDir)) {
            fs.delete(outDir, true);
        }

        FileOutputFormat.setOutputPath(job, outDir);
        job.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job.setOutputValueClass(org.apache.hadoop.io.IntWritable.class);
        job.setOutputFormatClass(org.apache.hadoop.mapreduce.lib.output.TextOutputFormat.class);

        // Add the JSON stopwords file to the distributed cache
        job.addCacheFile(new Path(stopWordsPath).toUri());

        long startTime = System.currentTimeMillis();
        try {
            boolean success = job.waitForCompletion(true);
            if (!success) {
                System.err.println("Job failed!");
                System.exit(-1);
            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            long totalWords = job.getCounters().findCounter("WordCount", "TotalWords").getValue();
            double wordsPerMinute = (totalWords / (elapsedTime / 60000.0));

            Path path = new Path(inputPath);
            FileStatus fileStatus = fs.getFileStatus(path);

            long inputFileSize = fileStatus.getLen();
            String inputFileName = fileStatus.getPath().getName();

            System.out.println("Stats:");
            System.out.println("Input File: " + inputFileName);
            System.out.println("Input File Size (bytes): " + inputFileSize);
            System.err.println("Language: " + lang);
            System.out.println("Total Words: " + totalWords);
            System.out.println("Elapsed Time (ms): " + elapsedTime);

            DecimalFormat df = new DecimalFormat("#");
            System.out.println("Words per Minute: " + df.format(wordsPerMinute));
            System.exit(0);
        } catch (InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
