package de.floriansymmank;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONObject;

import de.floriansymmank.utils.JsonUtils;

public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {

    private Map<String, List<String>> stopwords;

    // Runs once at the beginning of the task
    @Override
    protected void setup(Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.setup(context);

        String stopWordsPath = context.getConfiguration().get("stopWordsPath");
        String stopWordsFileName = new File(stopWordsPath).getName();
        

        // Load the stopwords from the distributed cache
        JSONObject stopwords_json = JsonUtils.loadJsonFile(context, stopWordsFileName);

        // System.out.println(stopwords_json.toString(4));

        Map<String, List<String>> stopwords = JsonUtils.convertJsonToMap(stopwords_json);

        // for (String name : stopwords.keySet()) {
        //     String key = name.toString();
        //     String value = stopwords.get(name).toString();
        //     System.out.println(key + " " + value);
        // }
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
