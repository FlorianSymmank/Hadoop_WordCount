package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WordCountMapperTest {

    private de.floriansymmank.WordCountMapper bookMapper;
    private Mapper<Object, Text, Text, IntWritable>.Context context;

    @Before
    public void setUp() {
        bookMapper = new WordCountMapper();
        bookMapper.stopwords = new java.util.HashSet<String>();
        context = mock(Mapper.Context.class);
    }

    @Test
    public void testMap() throws IOException, InterruptedException {
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
    }

    @Test
    public void testMapWithStopWords() throws IOException, InterruptedException {

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
    }
}
