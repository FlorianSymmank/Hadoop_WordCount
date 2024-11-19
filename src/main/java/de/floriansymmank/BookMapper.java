package de.floriansymmank;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class BookMapper extends Mapper<Object, Text, Text, IntWritable>
{
    // Runs once at the beginning of the task
    @Override
    protected void setup(Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.setup(context);
    }   
    
    // Runs once for each key-value pair in the input split
    @Override
    protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
    throws IOException, InterruptedException {
        Pattern pattern = Pattern.compile("[\\p{L}|\\d]+");
        Matcher matcher = pattern.matcher(value.toString());

        while (matcher.find()) {
            context.write(new Text(matcher.group()), new IntWritable(1));
        }
    }
    
    // Runs once at the end of the task
    @Override
    protected void cleanup(Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.cleanup(context);
    }
}
