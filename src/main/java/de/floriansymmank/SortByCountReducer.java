package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class SortByCountReducer extends Reducer<CompositeKey, NullWritable, CompositeKey, NullWritable> {

    public void reduce(CompositeKey key, Iterable<NullWritable> values, Context context)
            throws IOException, InterruptedException {
        context.write(key, NullWritable.get());
    }
}