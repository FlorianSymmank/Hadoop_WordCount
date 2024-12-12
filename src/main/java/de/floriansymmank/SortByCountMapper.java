package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SortByCountMapper extends Mapper<Object, Text, CompositeKey, NullWritable> {

    private CompositeKey compositeKey = new CompositeKey();

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        // Input format: word \t count
        String[] parts = value.toString().split("\t");
        if (parts.length == 2) {
            String word = parts[0];
            int count = Integer.parseInt(parts[1]);
            compositeKey = new CompositeKey(count, word);
            context.write(compositeKey, NullWritable.get());
            context.getCounter("KeyCount", "TotalKeys").increment(1);
            return;
        }

        throw new IOException("Invalid input format: " + value.toString());
    }
}
