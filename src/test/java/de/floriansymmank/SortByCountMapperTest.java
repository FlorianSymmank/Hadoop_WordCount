package de.floriansymmank;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SortByCountMapperTest {

    private Mapper<Object, Text, CompositeKey, NullWritable>.Context context;
    private SortByCountMapper mapper;

    @Before
    public void setUp() {
        mapper = new SortByCountMapper();
        context = mock(Mapper.Context.class);
    }

    @Test
    public void testMap() throws IOException, InterruptedException {
        String input = "word\t5";
        Text value = new Text(input);

        // Call the map method
        mapper.map(null, value, context);

        // Capture the output
        ArgumentCaptor<CompositeKey> keyCaptor = ArgumentCaptor.forClass(CompositeKey.class);
        ArgumentCaptor<NullWritable> valueCaptor = ArgumentCaptor.forClass(NullWritable.class);

        verify(context).write(keyCaptor.capture(), valueCaptor.capture());

        // Validate the output
        CompositeKey outputKey = keyCaptor.getValue();
        assertEquals(new IntWritable(5), outputKey.getCount());
        assertEquals(new Text("word"), outputKey.getWord());
    }

    @Test(expected = IOException.class)
    public void testMapWithInvalidInput() throws IOException, InterruptedException {
        String input = "word";
        Text value = new Text(input);

        // Call the map method
        mapper.map(null, value, context);
    }
}
