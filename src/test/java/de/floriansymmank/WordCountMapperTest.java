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
        context = mock(Mapper.Context.class);
    }

    @Test
    public void testMap() throws IOException, InterruptedException {
        Text value = new Text("Dies ist ein Beispiel-Satz mit E-Mail-Adresse test@example.com und Webseite www.example.com.");
        bookMapper.map(null, value, context);

        verify(context).write(new Text("Dies"), new IntWritable(1));
        verify(context).write(new Text("ist"), new IntWritable(1));
        verify(context).write(new Text("ein"), new IntWritable(1));
        verify(context).write(new Text("Beispiel"), new IntWritable(1));
        verify(context).write(new Text("Satz"), new IntWritable(1));
        verify(context).write(new Text("mit"), new IntWritable(1));
        verify(context).write(new Text("E"), new IntWritable(1));
        verify(context).write(new Text("Mail"), new IntWritable(1));
        verify(context).write(new Text("Adresse"), new IntWritable(1));
        verify(context).write(new Text("test"), new IntWritable(1));
        verify(context, times(2)).write(new Text("example"), new IntWritable(1));
        verify(context, times(2)).write(new Text("com"), new IntWritable(1));
        verify(context).write(new Text("und"), new IntWritable(1));
        verify(context).write(new Text("Webseite"), new IntWritable(1));
        verify(context).write(new Text("www"), new IntWritable(1));
    }
}
