package de.floriansymmank;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

        if (args.length != 5) {
            System.err.println("Usage: WordCounter <lang> <input path> <output path> <stopwords path> <stats file>");
            System.exit(-1);
        }

        String lang = args[0];
        String inputPath = args[1];
        String outputPath = args[2];
        String stopWordsPath = args[3];
        String statsFile = args[4];

        Configuration conf = new Configuration();
        conf.set("lang", lang);
        conf.set("stopWordsPath", stopWordsPath);

        Job job = Job.getInstance(conf, "Word Counter");

        // tweaking reduce tasks
        job.setNumReduceTasks(8);
        job.getConfiguration().setInt("mapreduce.reduce.memory.mb", 4096);
        job.getConfiguration().set("mapreduce.reduce.java.opts", "-Xmx3072m");
        job.getConfiguration().setBoolean("mapreduce.reduce.speculative", true); 
        job.getConfiguration().setBoolean("mapreduce.output.compress", true);
        job.getConfiguration().set("mapreduce.output.compress.codec", "org.apache.hadoop.io.compress.GzipCodec");

        // tweaking map tasks
        job.getConfiguration().setInt("mapreduce.map.memory.mb", 2048);
        job.getConfiguration().set("mapreduce.map.java.opts", "-Xmx1536m");
        job.getConfiguration().setBoolean("mapreduce.map.speculative", true);
        job.getConfiguration().setInt("mapreduce.task.io.sort.mb", 512);
        job.getConfiguration().setInt("mapreduce.task.io.sort.factor", 100);

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
            System.out.println("Language: " + lang);
            System.out.println("Total Words: " + totalWords);
            System.out.println("Elapsed Time (ms): " + elapsedTime);

            DecimalFormat df = new DecimalFormat("#");
            System.out.println("Words per Minute: " + df.format(wordsPerMinute));

            // Append to the stats file, create if not exists
            Path statsFilePath = new Path(statsFile);
            if (!fs.exists(statsFilePath)) {
                fs.create(statsFilePath).close(); // Create the file
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.append(statsFilePath), "UTF-8"))) {
                writer.write(String.format("{\"x\": %d, \"y\": %d}%n", inputFileSize, elapsedTime));
            }

            System.exit(0);
        } catch (InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
