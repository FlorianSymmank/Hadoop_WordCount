package de.floriansymmank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONObject;

import de.floriansymmank.utils.JsonUtils;

public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {

    Set<String> stopwords;

    // Runs once at the beginning of the task
    @Override
    protected void setup(Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.setup(context);

        String stopWordsPath = context.getConfiguration().get("stopWordsPath");
        String stopWordsFileName = new File(stopWordsPath).getName();

        // Load the stopwords from the distributed cache
        JSONObject stopwords_json = JsonUtils.loadJsonFile(context, stopWordsFileName);

        Map<String, List<String>> all_stopwords = JsonUtils.convertJsonToMap(stopwords_json);
        List<String> stopWordsList = all_stopwords.getOrDefault(context.getConfiguration().get("lang"), new ArrayList<String>());
        stopwords = new HashSet<String>(stopWordsList);

        if(stopwords.isEmpty())
            System.out.println("No stopwords found for " + context.getConfiguration().get("lang") +" language.");
    }

    // Runs once for each key-value pair in the input split
    @Override
    protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        Pattern pattern = Pattern.compile("[\\p{L}|\\d]+");
        Matcher matcher = pattern.matcher(value.toString());

        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);
            if (stopwords.contains(word)) {
                continue;
            }
            context.write(new Text(word), new IntWritable(1));
            context.getCounter("WordCount", "TotalWords").increment(1);
        }
    }

    // Runs once at the end of the task
    @Override
    protected void cleanup(Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        super.cleanup(context);
    }
}
