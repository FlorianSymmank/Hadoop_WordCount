package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class BookReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    // runs once at the beginning of the task
    @Override
    protected void setup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void reduce(Text arg0, Iterable<IntWritable> arg1,
            Reducer<Text, IntWritable, Text, IntWritable>.Context arg2) throws IOException, InterruptedException {

        int sum = 0;
        for (IntWritable value : arg1) {
            sum += value.get();
        }
        arg2.write(arg0, new IntWritable(sum));
    }

    // runs once at the end of the task
    @Override
    protected void cleanup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.cleanup(context);
    }

}
