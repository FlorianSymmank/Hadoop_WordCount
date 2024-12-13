package de.floriansymmank;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;
import de.floriansymmank.utils.JsonUtils;

public class WordCountMapperTest {

    private de.floriansymmank.WordCountMapper bookMapper;
    private Mapper<Object, Text, Text, IntWritable>.Context context;
    private Counter counter;

    @Before
    public void setUp() throws IOException {
        bookMapper = new WordCountMapper();

        JSONObject stopwords = JsonUtils.loadJsonFromFile("F:\\Projekts\\Hadoop_WordCount\\stopwords\\stopwords.json");
        Map<String, java.util.List<String>> all_stopwords = JsonUtils.convertJsonToMap(stopwords);
        bookMapper.stopwords = new HashSet<String>(all_stopwords.get("de"));

        context = mock(Mapper.Context.class);
        counter = mock(Counter.class);
        when(context.getCounter(anyString(), anyString())).thenReturn(counter);
    }

    @Test
    public void testMapWithoutStopwords() throws IOException, InterruptedException {

        bookMapper.stopwords = new HashSet<String>();

        Text value = new Text("Dies ist ein Beispiel-Satz mit E-Mail-Adresse test@example.com und Webseite www.example.com.");
        bookMapper.map(null, value, context);

        verify(context).write(new Text("dies"), new IntWritable(1));
        verify(context).write(new Text("ist"), new IntWritable(1));
        verify(context).write(new Text("ein"), new IntWritable(1));
        verify(context).write(new Text("beispiel"), new IntWritable(1));
        verify(context).write(new Text("satz"), new IntWritable(1));
        verify(context).write(new Text("mit"), new IntWritable(1));
        verify(context).write(new Text("e"), new IntWritable(1));
        verify(context).write(new Text("mail"), new IntWritable(1));
        verify(context).write(new Text("adresse"), new IntWritable(1));
        verify(context).write(new Text("test"), new IntWritable(1));
        verify(context, times(2)).write(new Text("example"), new IntWritable(1));
        verify(context, times(2)).write(new Text("com"), new IntWritable(1));
        verify(context).write(new Text("und"), new IntWritable(1));
        verify(context).write(new Text("webseite"), new IntWritable(1));
        verify(context).write(new Text("www"), new IntWritable(1));

        verify(counter, times(17)).increment(1);
    }

    @Test
    public void testMapWithSomeStopWords() throws IOException, InterruptedException {

        bookMapper.stopwords = new HashSet<String>();

        bookMapper.stopwords.add("ist");
        bookMapper.stopwords.add("mit");
        bookMapper.stopwords.add("und");

        Text value = new Text("Dies ist ein Beispiel-Satz mit E-Mail-Adresse test@example.com und Webseite www.example.com.");
        bookMapper.map(null, value, context);

        verify(context).write(new Text("dies"), new IntWritable(1));
        verify(context).write(new Text("ein"), new IntWritable(1));
        verify(context).write(new Text("beispiel"), new IntWritable(1));
        verify(context).write(new Text("satz"), new IntWritable(1));
        verify(context).write(new Text("e"), new IntWritable(1));
        verify(context).write(new Text("mail"), new IntWritable(1));
        verify(context).write(new Text("adresse"), new IntWritable(1));
        verify(context).write(new Text("test"), new IntWritable(1));
        verify(context, times(2)).write(new Text("example"), new IntWritable(1));
        verify(context, times(2)).write(new Text("com"), new IntWritable(1));
        verify(context).write(new Text("webseite"), new IntWritable(1));
        verify(context).write(new Text("www"), new IntWritable(1));

        verify(counter, times(14)).increment(1);
    }

    @Test
    public void testMapWithStopwordsAllDe() throws IOException, InterruptedException {
        assertEquals(bookMapper.stopwords.size(), 628);
        assert(bookMapper.stopwords.contains("für"));
        assert(bookMapper.stopwords.contains("das"));
        assert(bookMapper.stopwords.contains("dass"));
        assert(bookMapper.stopwords.contains("daß"));


        Text value = new Text("ich über die Gesetze der Phänomene hatte, jene kleinen Regungen, die vom Gehirn Aromasias ausgingen, für mich");
        bookMapper.map(null, value, context);

        verify(context).write(new Text("gesetze"), new IntWritable(1));
        verify(context).write(new Text("phänomene"), new IntWritable(1));
        verify(context).write(new Text("regungen"), new IntWritable(1));
        verify(context).write(new Text("gehirn"), new IntWritable(1));
        verify(context).write(new Text("aromasias"), new IntWritable(1));
        verify(context).write(new Text("ausgingen"), new IntWritable(1));

        verify(counter, times(6)).increment(1);
    }
}
